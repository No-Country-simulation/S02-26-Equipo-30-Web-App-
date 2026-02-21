from flask import Flask, request, jsonify, render_template_string
import pandas as pd
import numpy as np
import uuid
from datetime import datetime
import json

app = Flask(__name__)

# Ruta del dataset en tu workspace
DATA_PATH = "/workspaces/S02-26-Equipo-30-Web-App-/data/horsetrust_database.csv"

# Cargar CSV existente o crear DataFrame vacío
try:
    df = pd.read_csv(DATA_PATH)
except:
    df = pd.DataFrame()

# ---------------------------------------------------
# Diccionario de razas con rangos y parámetros
# ---------------------------------------------------
BREED_STANDARDS = {
    "Pura Sangre Inglés": {"height_m": (1.6,1.7), "weight_kg": (450,500), "length_m":2.4, "max_speed_kmh":71, "temperament":"Nervioso / Atlético", "main_use":"Carreras y Salto"},
    "Cuarto de Milla": {"height_m": (1.4,1.6), "weight_kg": (500,600), "length_m":2.3, "max_speed_kmh":70, "temperament":"Tranquilo / Sociable", "main_use":"Rodeo y Trabajo"},
    "Árabe": {"height_m": (1.4,1.5), "weight_kg": (350,450), "length_m":2.2, "max_speed_kmh":(55,60), "temperament":"Inteligente / Leal", "main_use":"Endurance (Raid)"},
    "Hannoveriano": {"height_m": (1.6,1.7), "weight_kg": (600,650), "length_m":2.5, "max_speed_kmh":50, "temperament":"Equilibrado", "main_use":"Salto y Doma"},
    "Akhal-Teke": {"height_m": (1.5,1.6), "weight_kg": (420,480), "length_m":2.3, "max_speed_kmh":60, "temperament":"Independiente", "main_use":"Resistencia"},
    "Pura Raza Española": {"height_m": (1.5,1.6), "weight_kg": (500,550), "length_m":2.4, "max_speed_kmh":(45,50), "temperament":"Noble / Dócil", "main_use":"Doma y Exhibición"},
    "Frisón": {"height_m": (1.6,1.7), "weight_kg": (600,800), "length_m":2.6, "max_speed_kmh":(40,45), "temperament":"Manso / Dispuesto", "main_use":"Tiro y Cine"},
    "Percherón": {"height_m": (1.6,1.8), "weight_kg": (800,1000), "length_m":2.8, "max_speed_kmh":(25,30), "temperament":"Muy calmado", "main_use":"Tiro pesado"},
    "Appaloosa": {"height_m": (1.4,1.6), "weight_kg": (450,550), "length_m":2.3, "max_speed_kmh":(55,60), "temperament":"Activo / Resistente", "main_use":"Rutas y Western"},
    "Shetland (Poni)": {"height_m": (0.7,1.1), "weight_kg": (150,200), "length_m":1.5, "max_speed_kmh":20, "temperament":"Testarudo / Fuerte", "main_use":"Niños y Compañía"}
}

# ---------------------------------------------------
# Función de cálculo de scores
# ---------------------------------------------------
def calculate_scores(df):
    horse_cols = [
        "h_sex","raza","height_m","weight_kg","length_m",
        "max_speed_kmh","h_temperament","h_category",
        "h_career_races","h_days_since_last_race","h_linaje"
    ]

    df["completeness"] = 1 - df[horse_cols].isna().sum(axis=1) / len(horse_cols)

    df["vet_score"] = np.where(
        df["vet_total_exams"] > 0,
        1 - (df["vet_major_issues"] / df["vet_total_exams"]),
        0.5
    )

    df["seller_score"] = (
        0.4 * pd.to_numeric(df["s_verified"], errors="coerce").fillna(0) +
        0.3 * (1 - np.minimum(pd.to_numeric(df["s_disputes"], errors="coerce").fillna(0)/10,1)) +
        0.3 * (1 - pd.to_numeric(df["s_flagged_fraud"], errors="coerce").fillna(0))
    )

    df["horse_trust_score"] = (
        0.4 * df["completeness"] +
        0.4 * df["vet_score"] +
        0.2 * df["seller_score"]
    )

    return df

# ---------------------------------------------------
# Formulario de ingreso
# ---------------------------------------------------
@app.route("/")
def form():
    breed_options = "".join([f'<option value="{b}">{b}</option>' for b in BREED_STANDARDS.keys()])
    listing_status_options = "".join([f'<option value="{s}">{s}</option>' for s in ["active","sold","withdrawn"]])

    return render_template_string("""
    <h2>Registrar Nuevo Caballo</h2>
    <form method="post" action="/add_horse">
        <h3>🐎 Datos del Caballo</h3>
        Nombre del caballo: <input name="horse_name" required><br>
        Fecha nacimiento: <input name="birth_date" type="date" required><br>
        Sexo:
        <select name="h_sex" required>
            <option value="Hembra">Hembra</option>
            <option value="Macho">Macho</option>
        </select><br>

        Raza:
        <select name="raza" id="raza" required onchange="updateBreedInfo()">
            {{ breed_options|safe }}
        </select><br>

        Altura (m): <input name="height_m" id="height_m" type="number" step="0.01" required><br>
        Peso (kg): <input name="weight_kg" id="weight_kg" type="number" required><br>
        Longitud (m): <input name="length_m" id="length_m" type="number" step="0.01" required><br>
        Velocidad máx (km/h): <input name="max_speed_kmh" id="max_speed_kmh" type="number" required><br>
        Temperamento: <input name="h_temperament" id="h_temperament" readonly><br>
        Main use: <input name="h_category" id="h_category" readonly><br>

        País actual: <input name="h_current_country" type="text" required><br>
        País de nacimiento: <input name="h_birth_country" type="text" required><br>

        Carreras totales: <input name="h_career_races" type="number" required><br>
        Días desde última carrera: <input name="h_days_since_last_race" type="number" required><br>

        Linaje:
        <select name="h_linaje">
            <option value="Sí">Sí</option>
            <option value="No">No</option>
        </select><br>

        <h3>💰 Listing</h3>
        Listing status:
        <select name="l_listing_status">
            {{ listing_status_options|safe }}
        </select><br>

        Precio USD: <input name="l_asking_price_usd" type="number" required><br>

        <h3>👤 Seller</h3>
        Verified:
        <select name="s_verified">
            <option value="1">Sí</option>
            <option value="0">No</option>
        </select><br>

        Disputes: <input name="s_disputes" type="number" required><br>
        Num listings: <input name="s_num_listings" type="number" required><br>

        Fraud flag:
        <select name="s_flagged_fraud">
            <option value="1">Sí</option>
            <option value="0">No</option>
        </select><br>

        <h3>🩺 Veterinaria</h3>
        Total exams: <input name="vet_total_exams" type="number" required><br>
        Major issues: <input name="vet_major_issues" type="number" required><br>
        Last Exam date: <input name="v_exam_date" type="date" required><br>

        <br><input type="submit" value="Registrar Caballo">
    </form>

    <script>
    const breedData = {{ breed_data | safe }};

    function updateBreedInfo(){
        const sel = document.getElementById('raza').value;
        const b = breedData[sel];

        document.getElementById('h_temperament').value = b.temperament;
        document.getElementById('h_category').value = b.main_use;

        let avg = (arr) => Array.isArray(arr) ? (arr[0]+arr[1])/2 : arr;

        document.getElementById('height_m').value = avg(b.height_m).toFixed(2);
        document.getElementById('weight_kg').value = avg(b.weight_kg);
        document.getElementById('length_m').value = avg(b.length_m).toFixed(2);
        document.getElementById('max_speed_kmh').value = avg(b.max_speed_kmh);
    }

    window.onload = updateBreedInfo;
    </script>
    """,
    breed_options=breed_options,
    listing_status_options=listing_status_options,
    breed_data=json.dumps(BREED_STANDARDS)
    )

# ---------------------------------------------------
@app.route("/add_horse", methods=["POST"])
def add_horse():
    global df
    new_horse = request.form.to_dict()

    new_horse["horse_id"] = str(uuid.uuid4())[:8]
    new_horse["listing_id"] = str(uuid.uuid4())[:8]
    new_horse["seller_id"] = str(uuid.uuid4())[:8]

    new_horse["s_created_at"] = datetime.now()
    new_horse["s_last_active_at"] = datetime.now()
    new_horse["l_created_at"] = datetime.now()

    numeric_cols = [
        "height_m","weight_kg","length_m","max_speed_kmh",
        "h_career_races","h_days_since_last_race",
        "l_asking_price_usd","s_verified","s_disputes",
        "s_num_listings","s_flagged_fraud",
        "vet_total_exams","vet_major_issues"
    ]

    for col in numeric_cols:
        new_horse[col] = float(new_horse[col])

    new_row = pd.DataFrame([new_horse])
    df = pd.concat([df, new_row], ignore_index=True)

    df = calculate_scores(df)
    df.to_csv(DATA_PATH, index=False)

    trust_score = df.iloc[-1]["horse_trust_score"] * 100

    if trust_score < 70:
        interpretation = "Se recomienda revisar y completar los datos. Al momento el Indice de confiabilidad es bajo."
    elif trust_score < 87:
        interpretation = "Confiable"
    else:
        interpretation = "Excelente"

    return jsonify({
        "horse_id": new_horse["horse_id"],
        "horse_trust_score_%": round(trust_score, 2),
        "interpretacion": interpretation
    })

# ---------------------------------------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
