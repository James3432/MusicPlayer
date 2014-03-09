package jk509.player;

/* Copyright (c) 2006 Timothy Wall, All Rights Reserved
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.geom.Area;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/** Install default drag images for JTable, JTree, JList, and JTextComponent.
 * 
 * @author twall
 */

public class SwingDragImages {

    static class ColorSelectionIcon implements Icon {
        private Color color;
        public ColorSelectionIcon(JColorChooser chooser) {
            this.color = chooser.getColor();
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g = g.create();
            g.translate(x, y);
            g.setColor(color);
            g.fillRect(0, 0, getIconWidth(), getIconHeight());
            g.setColor(Color.black);
            g.drawRect(0, 0, getIconWidth()-1, getIconHeight()-1);
        }
        public int getIconWidth() {
            return 16;
        }
        public int getIconHeight() {
            return 16;
        }
    }
    
    static class TableSelectionIcon implements Icon {
        private Area clip;
        private JTable table;
        public TableSelectionIcon(JTable table) {
            this.table = table;
            clip = new Area();
            for (int row=0;row < table.getRowCount();row++) {
                for (int col=0;col < table.getColumnCount();col++) {
                    if (table.isCellSelected(row, col))
                        clip.add(new Area(table.getCellRect(row, col, true)));
                }
            }
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g = g.create();
            g.translate(x, y);
            g.setClip(clip);
            try {
                table.setDoubleBuffered(false);
                table.paint(g);
            }
            finally {
                table.setDoubleBuffered(true);
            }
        }
        public int getIconWidth() {
            return table.getWidth();
        }
        public int getIconHeight() {
            return table.getHeight();
        }
    }
    
    static class ListSelectionIcon implements Icon {
        private Area clip;
        private JList list;
        public ListSelectionIcon(JList list) {
            this.list = list;
            clip = new Area();
            int[] selected = list.getSelectedIndices();
            for (int i=0;i < selected.length;i++) {
                int idx = selected[i];
                clip.add(new Area(list.getCellBounds(idx, idx)));
            }
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g = g.create();
            g.translate(x, y);
            g.setClip(clip);
            try {
                list.setDoubleBuffered(false);
                list.paint(g);
            }
            finally {
                list.setDoubleBuffered(true);
            }
        }
        public int getIconWidth() {
            return list.getWidth();
        }
        public int getIconHeight() {
            return list.getHeight();
        }
    }
    
    static class TextSelectionIcon implements Icon {
        private Area clip;
        private JTextComponent text;
        public TextSelectionIcon(JTextComponent text) {
            this.text= text;
            int start = text.getSelectionStart();
            int end = text.getSelectionEnd();
            if (start > end) {
                int tmp = start;
                start = end;
                end = tmp;
            }
            clip = new Area(new Rectangle(0, 0, text.getWidth(), text.getHeight()));
            try {
                Rectangle s = text.modelToView(start);
                Rectangle e = text.modelToView(end);
                clip = new Area(new Rectangle(s.x, s.y, e.x + e.width - s.x,
                                              e.y + e.height - s.y));
            }
            catch(BadLocationException e) {
            }
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g = g.create();
            g.translate(x, y);
            g.setClip(clip);
            try {
                text.setDoubleBuffered(false);
                text.paint(g);
            }
            finally {
                text.setDoubleBuffered(true);
            }
        }
        public int getIconWidth() {
            return text.getWidth();
        }
        public int getIconHeight() {
            return text.getHeight();
        }
    }
    
    static class TreeSelectionIcon implements Icon {

        private Area clip;
        private JTree tree;

        public TreeSelectionIcon(JTree tree) {
            this.tree = tree;
            int[] indices = tree.getSelectionRows();
            Arrays.sort(indices);
            clip = new Area();
            for (int i=0;i < indices.length;i++) {
                Rectangle r = tree.getRowBounds(indices[i]);
                clip.add(new Area(r));
            }
        }

        public int getIconWidth() {
            return tree.getWidth();
        }

        public int getIconHeight() {
            return tree.getHeight();
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g = g.create();
            g.translate(x, y);
            g.setClip(clip);
            try {
                tree.setDoubleBuffered(false);
                tree.paint(g);
            }
            finally {
                tree.setDoubleBuffered(true);
            }
        }
    }

    static class GlobalImageProvider implements DragSourceListener, DragSourceMotionListener {
        private GhostedDragImage ghost;
        public GlobalImageProvider() {
            DragSource.getDefaultDragSource().addDragSourceListener(this);
            DragSource.getDefaultDragSource().addDragSourceMotionListener(this);
        }
        private void check(DragSourceDragEvent e) {
            Component src = e.getDragSourceContext().getComponent();
            if (ghost == null && src instanceof JComponent) {
                JComponent c = (JComponent)src;
                Point screen = e.getLocation();
                Point origin = c.getLocationOnScreen(); 
                Point imageOffset = new Point(origin.x - screen.x,
                                              origin.y - screen.y);
                Icon icon = null;
                boolean opaque = false;
                if (src instanceof JTree) {
                    JTree tree = (JTree)c;
                    if (tree.getDragEnabled()) {
                        icon = new TreeSelectionIcon(tree);
                    }
                }
                else if (src instanceof JTable) {
                    JTable table = (JTable)c;
                    if (table.getDragEnabled()) {
                        icon = new TableSelectionIcon(table);
                    }
                }
                else if (src instanceof JList) {
                    JList list = (JList)c;
                    if (list.getDragEnabled()) {
                        icon = new ListSelectionIcon(list);
                    }
                }
                else if (src instanceof JTextComponent) {
                    JTextComponent text = (JTextComponent)c;
                    if (text.getDragEnabled()) {
                        icon = new TextSelectionIcon(text);
                    }
                }
                else if (src instanceof JColorChooser) {
                    JColorChooser chooser = (JColorChooser)c;
                    if (chooser.getDragEnabled()) {
                        icon = new ColorSelectionIcon(chooser);
                        imageOffset.x = -icon.getIconWidth()/2;
                        imageOffset.y = -icon.getIconHeight()/2;
                        opaque = true;
                    }
                }
                if (icon != null) {
                    ghost = new GhostedDragImage(c, e.getLocation(), icon, imageOffset);
                    if (opaque)
                        ghost.setAlpha(1f);
                }
            }
        }
        public void dragEnter(DragSourceDragEvent e) {
            check(e);
            if (ghost != null) {
                ghost.move(e.getLocation());
            }
        }
        public void dragOver(DragSourceDragEvent e) {
            check(e);
            if (ghost != null)
                ghost.move(e.getLocation());
        }
        public void dropActionChanged(DragSourceDragEvent e) {
            check(e);
            if (ghost != null)
                ghost.move(e.getLocation());
        }
        public void dragDropEnd(DragSourceDropEvent e) {
            if (ghost != null) {
                if (e.getDropSuccess()) {
                    ghost.dispose();
                }
                else {
                    ghost.returnToOrigin();
                }
                ghost = null;
            }
        }
        public void dragExit(DragSourceEvent e) {
        }
        public void dragMouseMoved(DragSourceDragEvent e) {
            check(e);
            if (ghost != null)
                ghost.move(e.getLocation());
        }
    }
    
}
