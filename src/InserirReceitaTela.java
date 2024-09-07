import ConexaoJDBC.ConexaoBD;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InserirReceitaTela extends JFrame {
    private JTextField nomeProduto;
    private JTextField quantidade;
    private JButton btnInserir;

    public InserirReceitaTela() {
        setTitle("Inserir Produto no Estoque");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        nomeProduto = new JTextField(20);
        quantidade = new JTextField(5);
        btnInserir = new JButton("Inserir");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Nome do Produto:"));
        panel.add(nomeProduto);
        panel.add(new JLabel("Quantidade:"));
        panel.add(quantidade);
        panel.add(btnInserir);

        btnInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inserirProdutoNoEstoque();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void inserirProdutoNoEstoque() {
        String nome = nomeProduto.getText();
        int qtd = Integer.parseInt(quantidade.getText());

        try (Connection conn = ConexaoBD.conectar()) {
            String sql = "INSERT INTO estoque (nome_produto, quantidade_disponivel) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setInt(2, qtd);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Produto inserido com sucesso!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao inserir produto.");
        }
    }

    public static void main(String[] args) {
        new InserirReceitaTela();
    }
}
