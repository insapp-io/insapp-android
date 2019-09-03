package fr.insapp.insapp.utility

import android.widget.MultiAutoCompleteTextView

/**
 * Created by thomas on 28/02/2017.
 * Kotlin rewrite on 03/09/2019.
 */

class TagTokenizer : MultiAutoCompleteTextView.Tokenizer {

    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        for (i in cursor downTo 1) {
            if (text[i - 1] == '@') {
                return i
            }
        }

        return cursor
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        return cursor
    }

    override fun terminateToken(text: CharSequence): CharSequence {
        var i = text.length

        while (i > 0 && text[i - 1] == ' ') {
            i--
        }

        return if (i > 0 && text[i - 1] == ' ') {
            text
        } else "$text "
    }
}