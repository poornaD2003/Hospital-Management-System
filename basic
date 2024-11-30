import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // Create the main JFrame
        JFrame frame = new JFrame("Hospital Managment System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel();
        label.setText("login");

// Create a button
        JButton button = new JButton("Pateint");
        button.setBounds(130, 100, 150, 40);
        JButton button2 = new JButton("Doctor");


        // Add action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open new page (new JFrame)
                NewPage newPage = new NewPage();
                newPage.setVisible(true);
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open new page (new JFrame)
                NewPage2 newPage = new NewPage2();
                newPage.setVisible(true);
            }
        });
// Add the button to the frame
        frame.add(label);
        frame.add(button);
        frame.add(button2);
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);
    }
}

// New Page Class
class NewPage extends JFrame {
    public NewPage() {
        // Set properties of the new frame
        setTitle("Paitent");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel("Welcome to the new page!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        System.out.println("hello");
        add(label);
}
}
class NewPage2 extends JFrame {
    public NewPage2() {
        // Set properties of the new frame
        setTitle("Doctor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel("Welcome to the new page!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}


