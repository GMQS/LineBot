package com.example.linebot.common;

import java.util.ArrayList;
import java.util.Iterator;

public class JointText {
    private String separator;

    public JointText(String separator) {
        this.separator = separator;
    }

    public String joint(ArrayList<String> texts) {
        if(texts.size() == 0){
            return "データがありません";
        }
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> il = texts.iterator(); il.hasNext();) {
            String text = il.next();
            sb.append(text);
            if (il.hasNext()) {
                sb.append(separator);
            }
        }

        return new String(sb);
    }

}
