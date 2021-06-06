package com.example.recuperacionuf2_sergiopujol;

import java.io.Serializable;

public class items implements Comparable<items>, Serializable {

    private String comentario;
    private String foto;

    public items(String comentario, String foto) {
        this.comentario = comentario;
        this.foto = foto;

    }
    public String getComentario() {
        return comentario;
    }
    public String getFoto() {
        return foto;
    }

    @Override
    public int compareTo(items o) {
        return 0;
    }
}
