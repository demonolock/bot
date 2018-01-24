package edu.technopolis.advanced.cashSaver.request;

import java.io.Serializable;

public interface RequestPayload extends Serializable {

    String toString();


    class EmptyPayload implements RequestPayload {
        private EmptyPayload() {

        }

    }
}
