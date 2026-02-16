from flask import Flask, request, jsonify, render_template_string
import pandas as pd

app = Flask(__name__)

# Ruta del dataset en tu workspace
DATA_PATH = "/workspaces/S02-26-Equipo-30-Web-App-/data/horsetrust_database_index.csv"

# Cargar CSV existente
try:
    df = pd.read_csv(DATA_PATH)
except FileNotFoundError:
    df = pd.DataFrame()
    print("⚠️ Archivo no encontrado, asegúrate de que exista en la ruta:", DATA_PATH)

# Establecer horse_id como índice
if not df.empty:
    df.set_index("horse_id", inplace=True)

# Columnas que queremos mostrar en la consulta
COLUMNS_TO_SHOW = [
    "horse_name",
    "height_m",
    "weight_kg",
    "length_m",
    "max_speed_kmh",
    "h_career_races",
    "h_days_since_last_race",
    "l_asking_price_usd",
    "s_disputes",
    "s_num_listings",
    "vet_total_exams",
    "vet_major_issues"
]

# Página principal con formulario para ingresar horse_id
@app.route("/", methods=["GET"])
def form():
    return render_template_string("""
        <h2>Consulta Horse Trust Score</h2>
        <form method="post" action="/get_horse">
            Ingrese horse_id: <input name="horse_id" required>
            <input type="submit" value="Consultar">
        </form>
    """)

# Endpoint para consultar horse_trust_score y columnas
@app.route("/get_horse", methods=["POST"])
def get_horse():
    horse_id = request.form.get("horse_id", "").strip()
    
    if horse_id not in df.index:
        return f"Horse ID '{horse_id}' no encontrado.", 404

    horse_data = df.loc[horse_id, COLUMNS_TO_SHOW].copy()

    # Trust score multiplicado por 100
    trust_score = df.loc[horse_id, "horse_trust_score"] * 100
    horse_data["horse_trust_score_%"] = round(trust_score, 2)

    # Interpretación
    if trust_score < 70:
        interpretation = "Se recomienda revisar más datos del caballo. Índice bajo"
    elif 70 <= trust_score < 87:
        interpretation = "Confiable"
    else:
        interpretation = "Excelente"
    horse_data["Interpretación"] = interpretation

    # Convertir a DataFrame para mostrar vertical y agregar horse_id
    horse_data_df = horse_data.to_frame().reset_index()
    horse_data_df.columns = ["Atributo", "Valor"]
    horse_id_row = pd.DataFrame([["horse_id", horse_id]], columns=["Atributo", "Valor"])
    horse_data_df = pd.concat([horse_id_row, horse_data_df], ignore_index=True)

    return render_template_string("""
        <h2>📊 Información del caballo</h2>
        <table border="1" cellpadding="5">
            <tr><th>Atributo</th><th>Valor</th></tr>
            {% for attr, val in data %}
                <tr><td>{{attr}}</td><td>{{val}}</td></tr>
            {% endfor %}
        </table>
        <br><a href="/">Volver</a>
    """, data=horse_data_df.values.tolist())

# Ejecutar app en Codespaces en puerto 5001
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)

