package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.AppInfo;

import javax.swing.*;
import java.awt.*;

public final class AboutPane {
  private AboutPane() {
  }

  public static void show(Component parent, String appName) {
    AppInfo info = AppInfo.getInstance();
    JLabel name = createLabel(info.getAppName(), Font.BOLD, 2, 8, 8);
    JLabel version = createLabel("Version " + info.getVersion(), Font.PLAIN, 0, 8, 4);
    JLabel copyright = createLabel(info.getCopyright(), Font.PLAIN, 0, 0, 4);

    var content = Box.createVerticalBox();
    content.add(name);
    content.add(version);
    content.add(copyright);

    JOptionPane.showMessageDialog(parent, content, "About " + appName, JOptionPane.PLAIN_MESSAGE);
  }

  private static JLabel createLabel(String text, int fontStyle, float fontSizeDelta, int topGap, int bottomGap) {
    var label = new JLabel(text);
    label.setBorder(BorderFactory.createEmptyBorder(topGap, 0, bottomGap, 0));
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    Font oldFont = label.getFont();
    label.setFont(oldFont.deriveFont(fontStyle, oldFont.getSize() + fontSizeDelta));
    return label;
  }
}
