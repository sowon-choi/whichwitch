package com.project.whichwitch;

import android.widget.EditText;
import android.widget.ToggleButton;

public class Data {
    private EditText todo;
    private ToggleButton share;

    public EditText getTodo(){
        return todo;
    }

        public void setTodo(EditText todo){
            this.todo=todo;
        }

    public ToggleButton getShare(){
        return share;
    }

     public void setShare(ToggleButton share){
        this.share=share;
    }
}