// A Tela Receitas será a tela de Cadastro dos Produtos no app

import ConexaoJDBC.ConexaoBD;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TelaReceitas extends JFrame {

    private JTextField txtNomeProduto;
    private JTextField txtQuantidade;
    private JTextField txtPreco;
    private JTextField txtCusto;
    private JButton btnAdicionar;

    public TelaReceitas() {
        setTitle("Inserir Produtos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        txtNomeProduto = new JTextField(20);
        txtQuantidade = new JTextField(20);
        txtPreco = new JTextField(20);
        txtCusto = new JTextField(20);
        btnAdicionar = new JButton("Adicionar ao Estoque");

        panel.add(new JLabel("Nome do Produto:"));
        panel.add(txtNomeProduto);
        panel.add(new JLabel("Quantidade:"));
        panel.add(txtQuantidade);
        panel.add(new JLabel("Preço:"));
        panel.add(txtPreco);
        panel.add(new JLabel("Custo de produção: "));
        panel.add(txtCusto);
        panel.add(btnAdicionar);

        add(panel);

        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarProdutoEstoque();
            }
        });

        setVisible(true);
    }

    private void adicionarProdutoEstoque() {
        String nomeProduto = txtNomeProduto.getText();
        String quantidadeStr = txtQuantidade.getText();
        String precoStr = txtPreco.getText();
        String custoStr = txtCusto.getText();


        if (nomeProduto.isEmpty() || quantidadeStr.isEmpty() || precoStr.isEmpty() || custoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantidade = Integer.parseInt(quantidadeStr);
            double preco = Double.parseDouble(precoStr);
            double custo = Double.parseDouble(custoStr);


            try (Connection conn = ConexaoBD.conectar()) {
                String sql = "INSERT INTO estoque (nome_produto, quantidade_disponivel, preco, custo) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nomeProduto);
                stmt.setInt(2, quantidade);
                stmt.setDouble(3, preco);
                stmt.setDouble(4, custo);

                int rowsInserted = stmt.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Produto adicionado ao estoque com sucesso!");
                    limparCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao adicionar produto ao estoque.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro de conexão com o banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade e Preço devem ser valores numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtNomeProduto.setText("");
        txtQuantidade.setText("");
        txtPreco.setText("");
        txtCusto.setText("");
    }
}
