package com.nc.horseretail.dto.communication;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter 
@Setter
public class ChatRequest {
    private UUID listingId; 
    private UUID senderId;  
    private String text;    
}
