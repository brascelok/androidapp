package com.blackrose.learnobjectivec.utilities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by mrnoo on 24/02/2016.
 */
public class Util {

    /*Theory activity*/
    public static final String INTENT_THEORY_DETAIL_HTML_NAME = "INTENT_THEORY_DETAIL_HTML_NAME";
    public static final String INTENT_THEORY_DETAIL_POS= "INTENT_THEORY_DETAIL_POS";
    public static final String INTENT_EXERCISE = "INTENT_EXERCISE";
    public static final String[] HTML_FILES = new String[]{
            "home.html", "home.html", "overview.html", "environment_setup.html", "program_structure.html",
            "basic_syntax.html", "data_types.html", "variables.html", "constants.html", "operators.html", "loops.html",
            "decision_making.html", "functions.html", "blocks.html", "numbers.html", "arrays.html", "pointers.html",
            "strings.html",  "structures.html", "preprocessors.html", "typedef.html", "type_casting.html",
            "log_handling.html", "error_handling.html",
            "classes_objects.html","classes_objects.html", "inheritance.html", "polymorphism.html", "data_encapsulation.html",
            "categories.html", "posing.html", "extensions.html", "protocols.html","dynamic_binding.html",
            "composite_objects.html", "foundation_framework.html", "fast_enumeration.html", "memory_management.html"
    };
    public static final String[] TITLES_WEB = new String[] {
            "Home","Home","Overview","Environment setup","Program structure","Basic Syntax","Data types","Variables",
            "Constants","Operators","Loops", "Desicion making","Functions","Blocks","Numbers","Arrays",
            "Pointers","Strings","Structures","Preprocessors","Typedef","Type casting","Log handling",
            "Error handling",
            "Classes & Objects","Classes & Objects", "Inheritance", "Polymorphism", "Data Encapsulation", "Categories", "Posing", "Extensions",
            "Protocols", "Dynamic Binding", "Composite Objects", "Foundation Framework", "Fast Enumeration", "Memory Management"
    };
    public static final String[] TITLES_BASIC = new String[]{
            "Home", "Overview", "Environment setup", "Program structure", "Basic Syntax", "Data types", "Variables",
            "Constants", "Operators", "Loops", "Desicion making", "Functions", "Blocks", "Numbers", "Arrays",
            "Pointers", "Strings", "Structures", "Preprocessors", "Typedef", "Type casting", "Log handling",
            "Error handling"
    };
    public static final String[] TITLES_ADVANCE = new String[]{
            "Classes & Objects", "Inheritance", "Polymorphism", "Data Encapsulation", "Categories", "Posing", "Extensions",
            "Protocols", "Dynamic Binding", "Composite Objects", "Foundation Framework", "Fast Enumeration", "Memory Management"
    };

    /*Question activity*/
    public static final List<String> listTopic = Collections.unmodifiableList(new ArrayList<String>(){{
        add("Development_Basics");
        add("App_States_and_Multitasking");
        add("State_App");
        add("Core_App_Objects");
    }});

    public static int getRandomNumberInRange(int min, int max){
        if(min > max){
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static void showAlertDialog(Context context, String title, String message){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
