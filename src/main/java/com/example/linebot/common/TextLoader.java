package com.example.linebot.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextLoader {
    private InputStream is;
    public TextLoader(final InputStream is){
        this.is = is;
    }

    public String load() throws IOException{
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        final ArrayList<String> lines = new ArrayList<>();
        while((line = br.readLine()) != null){
            lines.add(line);
        }
        br.close();
        return new JointText("\n").joint(lines);
    }
    
}
