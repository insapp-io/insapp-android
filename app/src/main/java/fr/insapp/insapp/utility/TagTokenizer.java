package fr.insapp.insapp.utility;

import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

/**
 * Created by thomas on 28/02/2017.
 */

public class TagTokenizer implements AppCompatMultiAutoCompleteTextView.Tokenizer {

    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        for (int i = cursor; i > 0; i--) {
            if (text.charAt(i - 1) == '@') {
                return i;
            }
        }

        return cursor;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        return cursor;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == ' ') {
            return text;
        }

        return text + " ";
    }
}