package net.cuprak.flatlaf;

import com.formdev.flatlaf.FlatLightLaf;
import groovy.lang.GroovyShell;

import javax.script.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Program that demonstrates the issue with SystemProperties and FlatLaf when running in a
 * signed application using OpenWebStart.
 * @author Ryan Cuprak
 */
public class TestProgram extends JFrame implements ActionListener {

    /**
     * Script to be run
     */
    private final JTextArea script = new JTextArea(10,80);

    private static final String JAVA_SCRIPT = "JavaScript";

    private static final String GROOVY = "Groovy";

    /**
     * Language chooser
     */
    private final JComboBox<String> languageChooser = new JComboBox<>(new String[] {JAVA_SCRIPT,GROOVY});

    private String groovyCode = "javax.swing.JOptionPane.showMessageDialog(null, \"Groovy works!\");";

    private String javaScriptCode = "javax.swing.JOptionPane.showMessageDialog(null, \"JavaScript works!\");";

    /**
     * JavaScript engine
     */
    jdk.nashorn.api.scripting.NashornScriptEngineFactory fact = new jdk.nashorn.api.scripting.NashornScriptEngineFactory();


    /**
     * True if JavaScript is selected.
     */
    private boolean javascript = true;

    /**
     * Groovy Shell
     */
    private final GroovyShell groovyShell = new GroovyShell();

    /**
     * Default Constructor
     */
    public TestProgram() {
        setTitle("Script Test");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Creates the UI
     */
    private void init() {
        FlatLightLaf.setup();
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 0; gc.weighty = 0;
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Language:"));
        panel.add(languageChooser);
        languageChooser.addActionListener(e -> {
            if(Objects.requireNonNull(languageChooser.getSelectedItem()).equals(JAVA_SCRIPT)) {
                // javascript selected
                if(!javascript) {
                    groovyCode = script.getText();
                    script.setText(javaScriptCode);
                }
                javascript = true;
            } else {
                // groovy selected
                if(javascript) {
                    javaScriptCode = script.getText();
                    script.setText(groovyCode);
                }
                javascript = false;
            }
        });
        content.add(panel,gc);
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 2; gc.fill = GridBagConstraints.BOTH; gc.weightx = 1; gc.weighty = 1;
        content.add(new JScrollPane(script),gc);
        gc.gridx = 1; gc.gridy = 2; gc.gridwidth = 0; gc.fill = GridBagConstraints.NONE;
        JButton runButton = new JButton("Run");
        content.add(runButton,gc);
        runButton.addActionListener(this);
        setContentPane(content);
        script.setText(javaScriptCode);
        pack();
        setVisible(true);
    }

    /**
     * Runs the script
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e)  {
        System.out.println("Version 1");
        try {
            if (Objects.requireNonNull(languageChooser.getSelectedItem()).equals(JAVA_SCRIPT)) {
                ScriptEngine engine = fact.getScriptEngine();
                engine.eval(script.getText());
            } else {
                groovyShell.evaluate(script.getText());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        final TestProgram tp = new TestProgram();
        SwingUtilities.invokeAndWait(tp::init);
    }
}

