package org.xjiop.oxygenaodmod;

import android.content.Context;
import android.content.ContextWrapper;

class LanguageContextWrapper extends ContextWrapper {

    private LanguageContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context) {
        Context mContext = Helper.setLanguage(context);
        return new LanguageContextWrapper(mContext);
    }
}
