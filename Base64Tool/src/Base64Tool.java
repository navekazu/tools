package tools.base64tool;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.SortedMap;
import java.io.UnsupportedEncodingException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

public class Base64Tool {
    private JTextArea base64Area;
    private JTextArea textArea;
    private JComboBox availableCharsetsComboBox;
    private DefaultComboBoxModel availableCharsetsComboBoxModel;
    private SortedMap availableCharsetsMap;

    private static final String DEFAULT_CHARSET = "UTF-8";

    public Base64Tool() {
        createAvailableCharsetsMap();
        createComponent();
    }

    public void createAvailableCharsetsMap() {
        // 使用可能なキャラセット
        availableCharsetsMap = Charset.availableCharsets();
        availableCharsetsComboBoxModel = new DefaultComboBoxModel();

        for (Iterator i = availableCharsetsMap.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            availableCharsetsComboBoxModel.addElement(name);
//            System.out.println(name);

            // エイリアス
            Charset c = Charset.forName(name);
            for (Iterator j = c.aliases().iterator(); j.hasNext();) {
                String alias = (String) j.next();
//                System.out.println(" " + alias);
            }
        }
    }

    private void createComponent() {
        JFrame frame = new JFrame("Base64Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createBase64Component(), createTextComponent());
        contentPane.add(splitPane, BorderLayout.CENTER);


        frame.pack();
        frame.setVisible(true);
    }

    private Component createBase64Component() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("Base64"), BorderLayout.NORTH);

        base64Area = new JTextArea(30, 50);
        panel.add(new JScrollPane(base64Area), BorderLayout.CENTER);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());

        availableCharsetsComboBox = new JComboBox(availableCharsetsComboBoxModel);

        if (availableCharsetsComboBoxModel.getIndexOf(DEFAULT_CHARSET)!=-1) {
            availableCharsetsComboBoxModel.setSelectedItem(DEFAULT_CHARSET);
        }

        panel2.add(availableCharsetsComboBox, BorderLayout.NORTH);

        JButton button = new JButton("Decode >>");
        button.addActionListener(new DecodeAction());
        panel2.add(button, BorderLayout.SOUTH);

        panel.add(panel2, BorderLayout.SOUTH);
        return panel;
    }

    private Component createTextComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("Text"), BorderLayout.NORTH);

        textArea = new JTextArea(30, 50);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton button = new JButton("<< Encode");
        button.addActionListener(new EncodeAction());
        panel.add(button, BorderLayout.SOUTH);

        return panel;
    }

    private class DecodeAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                byte[] src = base64Area.getText().getBytes();
                byte[] dest = decode(src);
                textArea.setText(new String(dest, DEFAULT_CHARSET));
            } catch(UnsupportedEncodingException e) {
            }
        }
    }

    private class EncodeAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                byte[] src = textArea.getText().getBytes(DEFAULT_CHARSET);
                byte[] dest = encode(src);
                base64Area.setText(new String(dest, (String)availableCharsetsComboBoxModel.getSelectedItem()));
            } catch(UnsupportedEncodingException e) {
            }
        }
    }

    private byte[] decode(byte[] src) {
        return Base64.decodeBase64(src);
    }

    private byte[] encode(byte[] src) {
        return Base64.encodeBase64(src);
    }

    public static void main(String[] args) {
        new Base64Tool();
    }
}
