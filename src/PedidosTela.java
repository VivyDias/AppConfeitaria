import ConexaoJDBC.ConexaoBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class PedidosTela extends JFrame {
    private JTable tabelaEstoque;
    private JTable tabelaPedido;
    private JTextField txtQuantidade;
    private JButton btnAdicionarProduto;
    private JComboBox<String> cbFormaPagamento;
    private JButton btnFinalizarPedido;
    private JButton btnVoltarMenu; // Botão para voltar ao menu principal
    private Vector<Vector<Object>> itensPedido = new Vector<>();
    private Vector<String> columnNamesPedido = new Vector<>();

    public PedidosTela() {
        setTitle("Realizar Pedido");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabelaEstoque = new JTable();
        txtQuantidade = new JTextField(5);
        btnAdicionarProduto = new JButton("Adicionar Produto");

        // Caixa de opções para forma de pagamento
        cbFormaPagamento = new JComboBox<>(new String[]{"Pix", "Dinheiro", "Ifood", "Cartão"});

        btnFinalizarPedido = new JButton("Finalizar Pedido");

        // Botão Voltar ao Menu Principal
        btnVoltarMenu = new JButton("Voltar ao Menu Principal");

        // Tabela para exibir produtos no pedido
        tabelaPedido = new JTable();
        columnNamesPedido.add("Produto");
        columnNamesPedido.add("Quantidade");
        columnNamesPedido.add("Valor Unitário");
        columnNamesPedido.add("Total");

        // Painel com GridBagLayout para controle flexível do layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Linha 1: Quantidade + Campo Quantidade + Botão Adicionar Produto
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Quantidade:"), gbc);

        gbc.gridx = 1;
        panel.add(txtQuantidade, gbc);

        gbc.gridx = 2;
        panel.add(btnAdicionarProduto, gbc);

        // Linha 2: Forma de Pagamento + ComboBox
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Forma de Pagamento:"), gbc);

        gbc.gridx = 1;
        panel.add(cbFormaPagamento, gbc);

        // Linha 3: Finalizar Pedido + Voltar ao Menu Principal
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnFinalizarPedido, gbc);

        gbc.gridx = 1;
        panel.add(btnVoltarMenu, gbc);

        // Adicionando tabelas e painel ao JFrame
        JScrollPane scrollPaneEstoque = new JScrollPane(tabelaEstoque);
        JScrollPane scrollPanePedido = new JScrollPane(tabelaPedido);
        add(scrollPaneEstoque, BorderLayout.NORTH);
        add(scrollPanePedido, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        btnAdicionarProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarProdutoAoPedido();
            }
        });

        btnFinalizarPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalizarPedido();
            }
        });

        // Ação do botão "Voltar ao Menu Principal"
        btnVoltarMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuPrincipal(); // Abre o menu principal
                dispose(); // Fecha a tela de pedidos
            }
        });

        carregarEstoque();
        atualizarTabelaPedido();
        setVisible(true);
    }

    private void carregarEstoque() {
        try (Connection conn = ConexaoBD.conectar()) {
            String sql = "SELECT id, nome_produto, quantidade_disponivel, preco FROM estoque";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Carregar dados na tabela
            Vector<Vector<Object>> data = new Vector<>();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Produto");
            columnNames.add("Quantidade Disponível");
            columnNames.add("Preço");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nome_produto"));
                row.add(rs.getInt("quantidade_disponivel"));
                row.add(rs.getBigDecimal("preco"));
                data.add(row);
            }

            tabelaEstoque.setModel(new DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void adicionarProdutoAoPedido() {
        int selectedRow = tabelaEstoque.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
            return;
        }

        int quantidade = Integer.parseInt(txtQuantidade.getText());
        String produto = tabelaEstoque.getValueAt(selectedRow, 1).toString();
        double valorUnitario = Double.parseDouble(tabelaEstoque.getValueAt(selectedRow, 3).toString());
        double total = quantidade * valorUnitario;

        Vector<Object> itemPedido = new Vector<>();
        itemPedido.add(produto);
        itemPedido.add(quantidade);
        itemPedido.add(valorUnitario);
        itemPedido.add(total);

        itensPedido.add(itemPedido);
        atualizarTabelaPedido();

        JOptionPane.showMessageDialog(this, "Produto adicionado ao pedido.");
    }

    private void atualizarTabelaPedido() {
        DefaultTableModel modelo = new DefaultTableModel(itensPedido, columnNamesPedido);
        tabelaPedido.setModel(modelo);
    }

    private void finalizarPedido() {
        double valorTotal = 0;
        for (Vector<Object> item : itensPedido) {
            valorTotal += (double) item.get(3);
        }

        String formaPagamento = (String) cbFormaPagamento.getSelectedItem();

        try (Connection conn = ConexaoBD.conectar()) {
            // Inserir pedido na base de dados e capturar o ID gerado
            String sqlPedido = "INSERT INTO pedidos (data_pedido, valor_total, forma_pagamento, status) VALUES (CURRENT_DATE, ?, ?, 'Finalizado')";
            PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            stmtPedido.setDouble(1, valorTotal);
            stmtPedido.setString(2, formaPagamento);
            stmtPedido.executeUpdate();

            // Capturar o ID do pedido gerado
            ResultSet generatedKeys = stmtPedido.getGeneratedKeys();
            int pedidoId = 0;
            if (generatedKeys.next()) {
                pedidoId = generatedKeys.getInt(1);
            }

            // Inserir itens do pedido na tabela itens_pedido
            for (Vector<Object> item : itensPedido) {
                String produto = (String) item.get(0);
                int quantidade = (int) item.get(1);
                double valorUnitario = (double) item.get(2);

                // Precisamos do ID do produto para associar corretamente o item ao pedido
                String sqlProduto = "SELECT id FROM estoque WHERE nome_produto = ?";
                PreparedStatement stmtProduto = conn.prepareStatement(sqlProduto);
                stmtProduto.setString(1, produto);
                ResultSet rsProduto = stmtProduto.executeQuery();
                int produtoId = 0;
                if (rsProduto.next()) {
                    produtoId = rsProduto.getInt("id");
                }

                String sqlItensPedido = "INSERT INTO itens_pedido (id_pedido, id_produto, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";
                PreparedStatement stmtItensPedido = conn.prepareStatement(sqlItensPedido);
                stmtItensPedido.setInt(1, pedidoId);
                stmtItensPedido.setInt(2, produtoId);
                stmtItensPedido.setInt(3, quantidade);
                stmtItensPedido.setDouble(4, valorUnitario);
                stmtItensPedido.executeUpdate();

                // Atualizar o estoque
                String sqlEstoque = "UPDATE estoque SET quantidade_disponivel = quantidade_disponivel - ? WHERE id = ?";
                PreparedStatement stmtEstoque = conn.prepareStatement(sqlEstoque);
                stmtEstoque.setInt(1, quantidade);
                stmtEstoque.setInt(2, produtoId);
                stmtEstoque.executeUpdate();
            }

            // Limpar tela
            itensPedido.clear();
            atualizarTabelaPedido();
            txtQuantidade.setText("");
            cbFormaPagamento.setSelectedIndex(0);
            carregarEstoque();

            JOptionPane.showMessageDialog(this, "Pedido finalizado com sucesso!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao finalizar pedido.");
        }
    }

    public static void main(String[] args) {
        new PedidosTela();
    }
}
