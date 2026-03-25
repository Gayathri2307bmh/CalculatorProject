import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MultiToolApp extends JFrame {

    public MultiToolApp() {
        setTitle("Smart Multi-Tool App");
        setSize(980, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 16));
        tabs.addTab("Calculator", new CalculatorPanel());
        tabs.addTab("BMI", new BMIPanel());
        tabs.addTab("Age", new AgePanel());
        tabs.addTab("Unit Converter", new ConverterPanel());
        tabs.addTab("Notes", new NotesPanel());

        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MultiToolApp::new);
    }
}

class AppTheme {
    static final Color BG = new Color(18, 18, 18);
    static final Color PANEL = new Color(28, 28, 28);
    static final Color PANEL_2 = new Color(36, 36, 36);
    static final Color TEXT = Color.WHITE;
    static final Color ACCENT = new Color(0, 153, 102);
    static final Color BLUE = new Color(70, 130, 180);
    static final Color RED = new Color(180, 60, 60);
    static final Color PURPLE = new Color(78, 76, 140);

    static void stylePanel(JComponent c) {
        c.setBackground(PANEL);
        c.setForeground(TEXT);
    }

    static void styleLabel(JLabel label, int size) {
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
    }

    static void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setBackground(PANEL_2);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    static void styleArea(JTextArea area) {
        area.setFont(new Font("Monospaced", Font.PLAIN, 16));
        area.setBackground(PANEL_2);
        area.setForeground(TEXT);
        area.setCaretColor(TEXT);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    static void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        combo.setBackground(PANEL_2);
        combo.setForeground(TEXT);
    }

    static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        return btn;
    }
}

class CalculatorPanel extends JPanel implements ActionListener, KeyListener {

    private final JTextField display;
    private final JTextArea historyArea;

    private final String[] buttons = {
            "C", "⌫", "(", ")", "√",
            "7", "8", "9", "/", "%",
            "4", "5", "6", "*", "^",
            "1", "2", "3", "-", "x²",
            "±", "0", ".", "+", "=",
            "Copy", "History Clear"
    };

    CalculatorPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(AppTheme.BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(AppTheme.BG);

        JLabel title = new JLabel("Smart Calculator");
        AppTheme.styleLabel(title, 24);

        display = new JTextField();
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("SansSerif", Font.BOLD, 28));
        display.setBackground(new Color(30, 30, 30));
        display.setForeground(Color.WHITE);
        display.setCaretColor(Color.WHITE);
        display.setBorder(new EmptyBorder(16, 14, 16, 14));
        display.addKeyListener(this);

        top.add(title, BorderLayout.NORTH);
        top.add(display, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(6, 5, 10, 10));
        center.setBackground(AppTheme.BG);

        for (String text : buttons) {
            JButton btn;
            if (text.equals("=")) {
                btn = AppTheme.createButton(text, AppTheme.ACCENT);
            } else if (text.equals("C") || text.equals("⌫") || text.equals("History Clear")) {
                btn = AppTheme.createButton(text, AppTheme.RED);
            } else if (text.equals("Copy")) {
                btn = AppTheme.createButton(text, AppTheme.BLUE);
            } else if (text.matches("[0-9.]")) {
                btn = AppTheme.createButton(text, new Color(50, 50, 50));
            } else {
                btn = AppTheme.createButton(text, AppTheme.PURPLE);
            }
            btn.addActionListener(this);
            center.add(btn);
        }

        add(center, BorderLayout.CENTER);

        historyArea = new JTextArea(8, 20);
        historyArea.setEditable(false);
        AppTheme.styleArea(historyArea);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                "History"
        ));
        scrollPane.getViewport().setBackground(new Color(36, 36, 36));

        add(scrollPane, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = ((JButton) e.getSource()).getText();
        handleInput(cmd);
    }

    private void handleInput(String cmd) {
        switch (cmd) {
            case "C":
                display.setText("");
                break;
            case "⌫":
                String text = display.getText();
                if (!text.isEmpty()) {
                    display.setText(text.substring(0, text.length() - 1));
                }
                break;
            case "=":
                calculateResult();
                break;
            case "√":
                try {
                    double value = evaluate(display.getText());
                    if (value < 0) {
                        display.setText("Error");
                        return;
                    }
                    double result = Math.sqrt(value);
                    addToHistory("√(" + value + ") = " + formatNumber(result));
                    display.setText(formatNumber(result));
                } catch (Exception ex) {
                    display.setText("Error");
                }
                break;
            case "x²":
                try {
                    double value = evaluate(display.getText());
                    double result = value * value;
                    addToHistory("(" + value + ")² = " + formatNumber(result));
                    display.setText(formatNumber(result));
                } catch (Exception ex) {
                    display.setText("Error");
                }
                break;
            case "%":
                try {
                    double value = evaluate(display.getText());
                    double result = value / 100.0;
                    addToHistory(value + "% = " + formatNumber(result));
                    display.setText(formatNumber(result));
                } catch (Exception ex) {
                    display.setText("Error");
                }
                break;
            case "±":
                toggleSign();
                break;
            case "Copy":
                copyDisplay();
                break;
            case "History Clear":
                historyArea.setText("");
                break;
            default:
                display.setText(display.getText() + cmd);
        }
    }

    private void calculateResult() {
        String expression = display.getText();
        if (expression.isEmpty()) {
            return;
        }
        try {
            double result = evaluate(expression);
            String formatted = formatNumber(result);
            addToHistory(expression + " = " + formatted);
            display.setText(formatted);
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    private void toggleSign() {
        String expr = display.getText().trim();
        if (expr.isEmpty()) {
            return;
        }
        try {
            double value = evaluate(expr);
            display.setText(formatNumber(-value));
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    private void copyDisplay() {
        String text = display.getText();
        if (!text.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            JOptionPane.showMessageDialog(this, "Copied: " + text);
        }
    }

    private void addToHistory(String entry) {
        historyArea.append(entry + "\n");
    }

    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    public static double evaluate(String expression) {
        List<String> postfix = infixToPostfix(expression);
        return evaluatePostfix(postfix);
    }

    private static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        expression = expression.replaceAll("\\s+", "");
        List<String> tokens = tokenize(expression);

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty()) {
                    operators.pop();
                }
            } else {
                while (!operators.isEmpty() && !operators.peek().equals("(") && hasHigherPrecedence(operators.peek(), token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    private static boolean hasHigherPrecedence(String stackOp, String currentOp) {
        int p1 = precedence(stackOp);
        int p2 = precedence(currentOp);
        return p1 > p2 || (p1 == p2 && !currentOp.equals("^"));
    }

    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                double b = stack.pop();
                double a = stack.pop();

                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        if (b == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        stack.push(a / b);
                        break;
                    case "^":
                        stack.push(Math.pow(a, b));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operator");
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return stack.pop();
    }

    private static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                number.append(ch);
            } else {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }

                if (ch == '-' && (i == 0 || "+-*/^(".indexOf(expr.charAt(i - 1)) != -1)) {
                    number.append(ch);
                } else {
                    tokens.add(String.valueOf(ch));
                }
            }
        }

        if (number.length() > 0) {
            tokens.add(number.toString());
        }

        return tokens;
    }

    private static boolean isNumber(String s) {
        if (s == null || s.isEmpty() || s.equals("-")) {
            return false;
        }
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char ch = e.getKeyChar();
        if ((ch >= '0' && ch <= '9') || "+-*/.^()".indexOf(ch) != -1) {
            display.setText(display.getText() + ch);
        } else if (ch == '\n') {
            calculateResult();
        } else if (ch == '\b') {
            String text = display.getText();
            if (!text.isEmpty()) {
                display.setText(text.substring(0, text.length() - 1));
            }
        } else if (ch == 27) {
            display.setText("");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            calculateResult();
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            display.setText("");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

class BMIPanel extends JPanel {
    BMIPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(6, 2, 12, 12));
        form.setBackground(AppTheme.BG);

        JLabel title = new JLabel("BMI Calculator");
        AppTheme.styleLabel(title, 24);

        JTextField weightField = new JTextField();
        JTextField heightField = new JTextField();
        AppTheme.styleField(weightField);
        AppTheme.styleField(heightField);

        JLabel resultLabel = new JLabel("Enter values and click Calculate");
        AppTheme.styleLabel(resultLabel, 18);

        JButton calcBtn = AppTheme.createButton("Calculate BMI", AppTheme.ACCENT);
        JButton clearBtn = AppTheme.createButton("Clear", AppTheme.RED);

        form.add(makeLabel("Weight (kg):"));
        form.add(weightField);
        form.add(makeLabel("Height (cm):"));
        form.add(heightField);
        form.add(new JLabel());
        form.add(new JLabel());
        form.add(calcBtn);
        form.add(clearBtn);
        form.add(makeLabel("Result:"));
        form.add(resultLabel);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBackground(AppTheme.BG);
        wrap.add(title, BorderLayout.NORTH);
        wrap.add(form, BorderLayout.CENTER);

        add(wrap, BorderLayout.NORTH);

        calcBtn.addActionListener(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText().trim());
                double heightCm = Double.parseDouble(heightField.getText().trim());
                double heightM = heightCm / 100.0;
                double bmi = weight / (heightM * heightM);

                String category;
                if (bmi < 18.5) {
                    category = "Underweight";
                } else if (bmi < 25) {
                    category = "Normal";
                } else if (bmi < 30) {
                    category = "Overweight";
                } else {
                    category = "Obese";
                }

                resultLabel.setText(String.format("BMI = %.2f (%s)", bmi, category));
            } catch (Exception ex) {
                resultLabel.setText("Invalid input");
            }
        });

        clearBtn.addActionListener(e -> {
            weightField.setText("");
            heightField.setText("");
            resultLabel.setText("Enter values and click Calculate");
        });
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        AppTheme.styleLabel(label, 18);
        return label;
    }
}

class AgePanel extends JPanel {
    AgePanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(7, 2, 12, 12));
        form.setBackground(AppTheme.BG);

        JLabel title = new JLabel("Age Calculator");
        AppTheme.styleLabel(title, 24);

        JTextField dayField = new JTextField();
        JTextField monthField = new JTextField();
        JTextField yearField = new JTextField();
        AppTheme.styleField(dayField);
        AppTheme.styleField(monthField);
        AppTheme.styleField(yearField);

        JLabel resultLabel = new JLabel("Enter DOB and click Calculate");
        AppTheme.styleLabel(resultLabel, 18);

        JButton calcBtn = AppTheme.createButton("Calculate Age", AppTheme.ACCENT);
        JButton clearBtn = AppTheme.createButton("Clear", AppTheme.RED);

        form.add(makeLabel("Day:"));
        form.add(dayField);
        form.add(makeLabel("Month:"));
        form.add(monthField);
        form.add(makeLabel("Year:"));
        form.add(yearField);
        form.add(new JLabel());
        form.add(new JLabel());
        form.add(calcBtn);
        form.add(clearBtn);
        form.add(makeLabel("Result:"));
        form.add(resultLabel);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBackground(AppTheme.BG);
        wrap.add(title, BorderLayout.NORTH);
        wrap.add(form, BorderLayout.CENTER);

        add(wrap, BorderLayout.NORTH);

        calcBtn.addActionListener(e -> {
            try {
                int day = Integer.parseInt(dayField.getText().trim());
                int month = Integer.parseInt(monthField.getText().trim());
                int year = Integer.parseInt(yearField.getText().trim());

                LocalDate dob = LocalDate.of(year, month, day);
                LocalDate today = LocalDate.now();
                Period age = Period.between(dob, today);

                resultLabel.setText(age.getYears() + " years, " + age.getMonths() + " months, " + age.getDays() + " days");
            } catch (Exception ex) {
                resultLabel.setText("Invalid date");
            }
        });

        clearBtn.addActionListener(e -> {
            dayField.setText("");
            monthField.setText("");
            yearField.setText("");
            resultLabel.setText("Enter DOB and click Calculate");
        });
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        AppTheme.styleLabel(label, 18);
        return label;
    }
}

class ConverterPanel extends JPanel {
    private final JTextField inputField;
    private final JLabel resultLabel;
    private final JComboBox<String> typeCombo;
    private final JComboBox<String> fromCombo;
    private final JComboBox<String> toCombo;

    ConverterPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Unit Converter");
        AppTheme.styleLabel(title, 24);

        JPanel form = new JPanel(new GridLayout(7, 2, 12, 12));
        form.setBackground(AppTheme.BG);

        typeCombo = new JComboBox<>(new String[]{"Length", "Temperature"});
        fromCombo = new JComboBox<>();
        toCombo = new JComboBox<>();
        AppTheme.styleCombo(typeCombo);
        AppTheme.styleCombo(fromCombo);
        AppTheme.styleCombo(toCombo);

        inputField = new JTextField();
        AppTheme.styleField(inputField);

        resultLabel = new JLabel("Enter value and convert");
        AppTheme.styleLabel(resultLabel, 18);

        updateUnits();

        JButton convertBtn = AppTheme.createButton("Convert", AppTheme.ACCENT);
        JButton clearBtn = AppTheme.createButton("Clear", AppTheme.RED);

        form.add(makeLabel("Type:"));
        form.add(typeCombo);
        form.add(makeLabel("From:"));
        form.add(fromCombo);
        form.add(makeLabel("To:"));
        form.add(toCombo);
        form.add(makeLabel("Value:"));
        form.add(inputField);
        form.add(convertBtn);
        form.add(clearBtn);
        form.add(makeLabel("Result:"));
        form.add(resultLabel);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBackground(AppTheme.BG);
        wrap.add(title, BorderLayout.NORTH);
        wrap.add(form, BorderLayout.CENTER);
        add(wrap, BorderLayout.NORTH);

        typeCombo.addActionListener(e -> updateUnits());

        convertBtn.addActionListener(e -> convert());
        clearBtn.addActionListener(e -> {
            inputField.setText("");
            resultLabel.setText("Enter value and convert");
        });
    }

    private void updateUnits() {
        fromCombo.removeAllItems();
        toCombo.removeAllItems();

        String type = (String) typeCombo.getSelectedItem();
        if ("Length".equals(type)) {
            String[] units = {"Meter", "Kilometer", "Centimeter"};
            for (String unit : units) {
                fromCombo.addItem(unit);
                toCombo.addItem(unit);
            }
        } else {
            String[] units = {"Celsius", "Fahrenheit", "Kelvin"};
            for (String unit : units) {
                fromCombo.addItem(unit);
                toCombo.addItem(unit);
            }
        }
    }

    private void convert() {
        try {
            double value = Double.parseDouble(inputField.getText().trim());
            String type = (String) typeCombo.getSelectedItem();
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();

            double result;
            if ("Length".equals(type)) {
                double meters = toMeters(value, from);
                result = fromMeters(meters, to);
            } else {
                double celsius = toCelsius(value, from);
                result = fromCelsius(celsius, to);
            }

            resultLabel.setText(String.format("%.4f", result));
        } catch (Exception ex) {
            resultLabel.setText("Invalid input");
        }
    }

    private double toMeters(double value, String from) {
        switch (from) {
            case "Kilometer":
                return value * 1000;
            case "Centimeter":
                return value / 100;
            default:
                return value;
        }
    }

    private double fromMeters(double value, String to) {
        switch (to) {
            case "Kilometer":
                return value / 1000;
            case "Centimeter":
                return value * 100;
            default:
                return value;
        }
    }

    private double toCelsius(double value, String from) {
        switch (from) {
            case "Fahrenheit":
                return (value - 32) * 5 / 9;
            case "Kelvin":
                return value - 273.15;
            default:
                return value;
        }
    }

    private double fromCelsius(double value, String to) {
        switch (to) {
            case "Fahrenheit":
                return (value * 9 / 5) + 32;
            case "Kelvin":
                return value + 273.15;
            default:
                return value;
        }
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        AppTheme.styleLabel(label, 18);
        return label;
    }
}

class NotesPanel extends JPanel {
    NotesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppTheme.BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Quick Notes");
        AppTheme.styleLabel(title, 24);

        JTextArea notesArea = new JTextArea();
        AppTheme.styleArea(notesArea);

        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.getViewport().setBackground(new Color(36, 36, 36));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(AppTheme.BG);

        JButton clearBtn = AppTheme.createButton("Clear Notes", AppTheme.RED);
        JButton copyBtn = AppTheme.createButton("Copy Notes", AppTheme.BLUE);

        clearBtn.addActionListener(e -> notesArea.setText(""));
        copyBtn.addActionListener(e -> {
            String text = notesArea.getText();
            if (!text.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                JOptionPane.showMessageDialog(this, "Notes copied");
            }
        });

        buttons.add(clearBtn);
        buttons.add(copyBtn);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
}
