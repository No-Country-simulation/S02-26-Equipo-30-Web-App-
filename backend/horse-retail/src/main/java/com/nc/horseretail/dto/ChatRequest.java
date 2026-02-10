package com.nc.horseretail.dto.communication;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter 
@Setter
public class ChatRequest {
    private UUID listingId; // El anuncio del caballo
    private UUID senderId;  // El que envía
    private String text;    // El contenido
}