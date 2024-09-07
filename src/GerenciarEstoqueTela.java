import ConexaoJDBC.ConexaoBD;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class GerenciarEstoqueTela extends JFrame {
    private JTable tabelaEstoque;
    private JButton btnAtualizarQuantidade;
    private JButton btnVoltarMenu;
    private JTextField txtProdutoId;
    private JTextField txtNovaQuantidade;

    public GerenciarEstoqueTela() {
        setTitle("Gerenciar Estoque");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabelaEstoque = new JTable();
        btnAtualizarQuantidade = new JButton("Atualizar Quantidade");
        txtProdutoId = new JTextField(5);
        txtNovaQuantidade = new JTextField(5);

        // Botão Voltar
        btnVoltarMenu = new JButton("Voltar ao Menu Principal");

        JPanel panel = new JPanel();
        panel.add(new JLabel("ID do Produto:"));
        panel.add(txtProdutoId);
        panel.add(new JLabel("Nova Quantidade:"));
        panel.add(txtNovaQuantidade);
        panel.add(btnAtualizarQuantidade);
        panel.add(btnVoltarMenu);

        JScrollPane scrollPane = new JScrollPane(tabelaEstoque);
        add(scrollPane, "North");
        add(panel, "South");

        btnAtualizarQuantidade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarQuantidadeProduto();
            }
        });

        btnVoltarMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuPrincipal();
                dispose();
            }
        });

        carregarEstoque();
        setVisible(true);
    }

    private void carregarEstoque() {
        try (Connection conn = ConexaoBD.conectar()) {
            String sql = "SELECT id, nome_produto, quantidade_disponivel, preco, custo FROM estoque";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            Vector<Vector<Object>> data = new Vector<>();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Produto");
            columnNames.add("Quantidade Disponível");
            columnNames.add("Preço");
            columnNames.add("Custo");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nome_produto"));
                row.add(rs.getInt("quantidade_disponivel"));
                row.add(rs.getBigDecimal("preco"));
                row.add(rs.getBigDecimal("custo"));
                data.add(row);
            }

            tabelaEstoque.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void atualizarQuantidadeProduto() {
        int id = Integer.parseInt(txtProdutoId.getText());
        int novaQuantidade = Integer.parseInt(txtNovaQuantidade.getText());

        try (Connection conn = ConexaoBD.conectar()) {
            String sql = "UPDATE estoque SET quantidade_disponivel = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Quantidade atualizada com sucesso!");
            carregarEstoque();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar quantidade.");
        }
    }

    public static void main(String[] args) {
        new GerenciarEstoqueTela();
    }
}
