package cn.syj.emusearch.ui.layout;

import java.awt.*;

/**
 * @author syj
 **/
public final class GBC extends GridBagConstraints {

    public GBC(int gridx, int gridy) {
        this(gridx, gridy, 1, 1);
    }

    public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
    }

    public GBC setWeight(int x, int y) {
        this.weightx = x;
        this.weighty = y;
        return this;
    }

    public GBC setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    public GBC setFill(int fill) {
        this.fill = fill;
        return this;
    }

    public GBC setInsets(int top, int left, int bottom, int right) {
        this.insets.set(top, left, bottom, right);
        return this;
    }

    public GBC setInsets(Insets insets) {
        this.insets = insets;
        return this;
    }

}
