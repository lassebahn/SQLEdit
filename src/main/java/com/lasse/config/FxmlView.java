package com.lasse.config;

/**
 * Auflistung aller FXML-Views
 * @author Lasse Schöttner
 *
 */
public enum FxmlView {

    SQLEDIT {
        @Override
        public String getFxmlPath() {
            return "/com/lasse/view/SqlEditView.fxml";
        }
    },

    FELDERVIEW {
        @Override
        public String getFxmlPath() {
            return "/com/lasse/view/FelderView.fxml";
        }
    },

    DETAILVIEW {
        @Override
        public String getFxmlPath() {
            return "/com/lasse/view/DetailView.fxml";
        }
    },

    SERVEREDITVIEW {
        @Override
        public String getFxmlPath() {
            return "/com/lasse/view/ServerEditView.fxml";
        }
    },

    SERVERLISTEVIEW {
    	@Override
        public String getFxmlPath() {
            return "/com/lasse/view/ServerListeView.fxml";
        }
    },

    SQLABFRAGEEXEC {
    	@Override
        public String getFxmlPath() {
            return "/com/lasse/view/SqlAbfrageExec.fxml";
        }
    },

    SQLABFRAGENVIEW {
    	@Override
        public String getFxmlPath() {
            return "/com/lasse/view/SqlAbfragenView.fxml";
        }
    },

    SQLABFRAGEVIEW {
    	@Override
        public String getFxmlPath() {
            return "/com/lasse/view/SqlAbfrageView.fxml";
        }
    };

    public abstract String getFxmlPath();
}
