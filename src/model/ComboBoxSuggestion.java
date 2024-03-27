
package model;

import javax.swing.JComboBox;

import swing.StatusComboUI;

public class ComboBoxSuggestion<E> extends JComboBox<E> {
    public ComboBoxSuggestion(){
        setUI(new StatusComboUI());

    }
}
