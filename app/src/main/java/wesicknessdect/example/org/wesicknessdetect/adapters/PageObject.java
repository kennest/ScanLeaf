package wesicknessdect.example.org.wesicknessdetect.adapters;

public class PageObject {
    private String mTitleResId;
    private int mLayoutResId;

    public PageObject(String mTitleResId, int mLayoutResId) {
        this.mTitleResId = mTitleResId;
        this.mLayoutResId = mLayoutResId;
    }

    public String getmTitleResId() {
        return mTitleResId;
    }

    public void setmTitleResId(String mTitleResId) {
        this.mTitleResId = mTitleResId;
    }

    public int getmLayoutResId() {
        return mLayoutResId;
    }

    public void setmLayoutResId(int mLayoutResId) {
        this.mLayoutResId = mLayoutResId;
    }
}
