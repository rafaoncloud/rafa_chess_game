/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rafa_chess_game.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import rafa_chess_game.model.MoveLog;
import rafa_chess_game.model.board.Board;
import rafa_chess_game.model.board.Move;

/**
 *
 * @author henri
 */
public class GameHistoryPanel extends JPanel {

    private final DataModel dataModel;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    public GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        dataModel = new DataModel();
        JTable table = new JTable(dataModel);
        table.setRowHeight(15);
        scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void redo(Board board, MoveLog moveHistory) {
        int currentRow = 0;
        dataModel.clear();

        for (Move move : moveHistory.getMoves()) {
            if (move.getMovedPiece() == null) {
                continue;
            }
            String moveText = move.toString();
            if (move.getMovedPiece().getPieceAllegiance().isWhite()) {
                dataModel.setValueAt(moveText, currentRow, 0);
            } else if (move.getMovedPiece().getPieceAllegiance().isBlack()) {
                dataModel.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0) {
            Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            String moveText = lastMove.toString();
            if (lastMove.getMovedPiece() != null) {
                if (lastMove.getMovedPiece().getPieceAllegiance().isWhite()) {
                    this.dataModel.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
                } else if (lastMove.getMovedPiece().getPieceAllegiance().isBlack()) {
                    this.dataModel.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
                }
            }
        }

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(Board board) {
        if (board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if (board.currentPlayer().isInCheck()) {
            return "+";
        }
        return " ";

    }

    private static class DataModel extends DefaultTableModel {

        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        public DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (values == null) {
                return 0;
            }
            return values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Row currentRow = this.values.get(row);
            if (column == 0) {
                return currentRow.getWhiteMove();
            } else if (column == 1) {
                return currentRow.getBlackMove();
            }

            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            Row currentRow;
            if (values.size() <= row) {
                currentRow = new Row();
                values.add(currentRow);
            } else {
                currentRow = values.get(row);
            }

            if (column == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else if (column == 1) {
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }

    }

    private static class Row {

        private String whiteMove;
        private String blackMove;

        public Row() {
        }

        public String getWhiteMove() {
            return whiteMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }

    }
}
