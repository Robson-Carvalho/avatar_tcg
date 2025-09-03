package com.oak.oak_protocol;

import java.io.IOException;
import java.util.Map;

public interface OakRealTimeHandler {
    // Chamado quando a conexão abre
    default void onOpen(OakRealTime ws) throws IOException {}

    // Chamado quando recebe uma mensagem (JSON ou texto)
    default void onMessage(OakRealTime ws, Map<String, Object> message) throws Exception {}

    // Chamado quando a conexão fecha
    default void onClose(OakRealTime ws) throws IOException {}
}
