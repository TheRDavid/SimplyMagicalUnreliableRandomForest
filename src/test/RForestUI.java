package test;

import ir.DataSet.SplitResult;
import ir.Element;
import ir.RForest;
import ir.RTree;
import ir.RTree.RNode;
import ir.SplitPoint;
import ir.Vector3;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class RForestUI<T extends Comparable<T>> extends JFrame {

	private JTree tree;
	private JButton testButton = new JButton("Categorize Value");
	private RForest<T> randomForest;
	private JPopupMenu dataPopup = new JPopupMenu();
	private JMenuItem graphItem = new JMenuItem("Graph");
	private JMenuItem splitItem = new JMenuItem("All Splits");
	private RNode selectedNode = null;
	Class type;

	public RForestUI(final RForest<T> forest) {
		dataPopup.add(graphItem);
		graphItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DataGraph(selectedNode.getSample().getElements(), selectedNode
						.getSplitPoint(),
						selectedNode.getSplitPoint().getFeatureIndex() != 0 ? 0 : 1,
						selectedNode.nodeID);

			}
		});
		dataPopup.add(splitItem);
		splitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SplitFinder(selectedNode);
			}
		});
		randomForest = forest;
		type = randomForest.getData().getElements().get(0).getAttribute(0).getClass();
		final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
				"RANDOM FOREST");
		int treeNum = 0;

		for (RTree<T> rt : forest.getTrees()) {
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Tree #"
					+ treeNum++);
			fillTreeNode(treeNode, rt.getRoot());
			rootNode.add(treeNode);
		}
		System.out.println("Done building tree, now building data node");
		DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode("DATA");

		ArrayList<Element<T>> elements = forest.getData().getElements();
		System.out.println("Sorting by category");
		Collections.sort(elements, new Comparator<Element>() {
			@Override
			public int compare(Element arg0, Element arg1) {
				return arg0.getCategory() - arg1.getCategory();
			};
		});
		System.out.println("Done sorting by category, now adding to dataNode");
		for (Element e : elements)
			dataNode.add(new DefaultMutableTreeNode(e.toString()));
		System.out.println("Added to dataNode");
		rootNode.add(dataNode);
		System.out.println("Done building data node, now building TreeUI");

		tree = new JTree(rootNode);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				/* if nothing is selected */
				selectedNode = null;
				if (node != null && node.getUserObject() instanceof RTree.RNode) {
					selectedNode = (RTree.RNode) node.getUserObject();

				}

			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				System.out.println(selectedNode);
				if (selectedNode != null && arg0.getButton() == 3) {
					splitItem.setEnabled(selectedNode.getLeft() != null);
					dataPopup.show(RForestUI.this, arg0.getX(), arg0.getY());
				}
			}
		});
		System.out.println("Done building TreeUI");
		add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
		add(testButton, BorderLayout.SOUTH);

		testButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String) JOptionPane.showInputDialog("Insert Values",
						"v0,v1,v2,...");
				s = s.replaceAll(" ", "");
				T attribs[] = (T[]) new Object[forest.getData().featureCount()];

				if (type.equals(Double.class)) {
					StringTokenizer tok = new StringTokenizer(s, ",");
					for (int i = 0; i < attribs.length; i++) {
						attribs[i] = (T) new Double(Double.parseDouble(tok.nextToken()));
					}
				} else if (type.equals(Vector3.class)) {
					StringTokenizer tok = new StringTokenizer(s, ";");
					for (int i = 0; i < attribs.length; i++) {
						String[] vectorDescription = tok.nextToken().split(",");
						attribs[i] = (T) new Vector3(Double
								.parseDouble(vectorDescription[0]), Double
								.parseDouble(vectorDescription[1]), Double
								.parseDouble(vectorDescription[2])); // totally save
					}
				}

				JOptionPane.showMessageDialog(RForestUI.this,
						"Category: " + forest.categorize(new Element<T>(attribs)));

			}
		});

		pack();
		setSize(800, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	int temp = 0;

	private void fillTreeNode(DefaultMutableTreeNode treeNode, RTree.RNode node) {
		int lvl = temp++;
		System.out.println("Filling Node " + lvl);
		if (node.getLeft() != null) {

			DefaultMutableTreeNode leftTreeNode = new DefaultMutableTreeNode(
					"LEFT Feature: " + node.getSplitPoint().getFeatureIndex() + " <"
							+ node.getSplitPoint().getSplitValue() + " => "
							+ node.getLeft().getCategory());
			fillTreeNode(leftTreeNode, node.getLeft());
			DefaultMutableTreeNode rightTreeNode = new DefaultMutableTreeNode(
					"RIGHT Feature: " + node.getSplitPoint().getFeatureIndex() + " >="
							+ node.getSplitPoint().getSplitValue() + " => "
							+ node.getRight().getCategory());
			fillTreeNode(rightTreeNode, node.getRight());
			treeNode.add(leftTreeNode);
			treeNode.add(rightTreeNode);
		}
		// insert Data-leafs

		DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(node);

		ArrayList<Element> elements = node.getSample().getElements();
		Collections.sort(elements, new Comparator<Element>() {
			@Override
			public int compare(Element arg0, Element arg1) {
				return arg0.getCategory() - arg1.getCategory();
			};
		});
		for (Element e : elements)
			dataNode.add(new DefaultMutableTreeNode(e.toString()));
		treeNode.add(dataNode);
		System.out.println("Done Filling Node " + lvl);
	}

	private void collapseTree(JTree tree, TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				collapseTree(tree, path);
			}
		}
		tree.collapsePath(parent);
	}

	private static Font fatFont = new Font("Arial", Font.BOLD, 14);

	class DataGraph extends JDialog {
		private double smallestX = 0, largestX = 0, smallestY = 0, largestY = 0,
				rangeX = 0, rangeY = 0;
		double midX = 0, midY = 0;
		private JPanel canvas = new JPanel() {
			protected void paintComponent(java.awt.Graphics arg0) {
				super.paintComponent(arg0);
				Graphics2D g = (Graphics2D) arg0;

				if (type.equals(Double.class)) {

					int xStep = 60;
					int yStep = 60;
					int ovalRadius = 4;

					g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
					g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

					for (int x = 0; x < getWidth(); x += xStep) {
						g.drawLine(x, getHeight() / 2 - 10, x, getHeight() / 2 + 10);
						String valString = String.format("%.2f", smallestX
								+ (rangeX * ((double) x / getWidth())));
						int valStringWidth = g.getFontMetrics().stringWidth(valString);
						g.drawString(valString, x - valStringWidth / 2,
								getHeight() / 2 - 25);
					}

					for (int y = 0; y < getHeight(); y += yStep) {
						g.drawLine(getWidth() / 2 - 10, y, getWidth() / 2 + 10, y);
						String valString = String.format("%.2f", smallestY + rangeY
								* ((double) y / getHeight()));
						int valStringWidth = g.getFontMetrics().stringWidth(valString);
						g.drawString(valString, getWidth() / 2 - 15 - valStringWidth, y);
					}

					g.drawLine(0, (int) mouseY, getWidth(), (int) mouseY);
					g.drawLine((int) mouseX, 0, (int) mouseX, getHeight());
					g.drawString(
							String.format("%.2f", mouseX / getWidth() * rangeX
									+ smallestX)
									+ " x "
									+ String.format("%.2f", mouseY / getHeight() * rangeY
											+ smallestY), (int) mouseX - 40,
							(int) mouseY - 40);

					g.setFont(fatFont);

					for (Element<Double> e : elements) {
						int x = (int) Math
								.round(((e.getAttribute(xFeatureIndex) - smallestX)
										/ rangeX * getWidth()));
						int y = (int) Math
								.round(((e.getAttribute(yFeatureIndex) - smallestY)
										/ rangeY * getHeight()));
						int c = e.getCategory();
						g.fillOval(x - ovalRadius, y - ovalRadius, ovalRadius * 2,
								ovalRadius * 2);
						g.drawString(c + "", x - ovalRadius - 5, y - ovalRadius - 5);
					}
				} else {
					g.drawString("Unable to visualize,  sry", 100, 100);
				}
			};
		};

		private ArrayList<Element> elements;
		private SplitPoint splitPoint;
		private int yFeatureIndex, xFeatureIndex;
		private double mouseX = 0, mouseY = 0;
		private JPanel controlPanel = new JPanel(new FlowLayout());
		private JComboBox<Integer> xCategory, yCategory;

		public DataGraph(ArrayList<Element> dat, SplitPoint split, int yFeature, long id) {
			splitPoint = split;
			xFeatureIndex = splitPoint.getFeatureIndex();
			yFeatureIndex = yFeature;
			elements = dat;

			calcRanges();

			canvas.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent arg0) {
					mouseX = arg0.getX();
					mouseY = arg0.getY();
					canvas.repaint();
				}
			});
			Integer[] features = new Integer[randomForest.getData().featureCount()];
			for (int i = 0; i < features.length; i++)
				features[i] = i;
			xCategory = new JComboBox<>(features);
			yCategory = new JComboBox<>(features);

			xCategory.setSelectedIndex(split.getFeatureIndex());
			yCategory.setSelectedIndex(yFeatureIndex);

			controlPanel.add(new JLabel("X-Feature:"));
			controlPanel.add(xCategory);
			controlPanel.add(new JLabel("Y-Feature:"));
			controlPanel.add(yCategory);

			xCategory.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					xFeatureIndex = xCategory.getSelectedIndex();
					calcRanges();
					canvas.repaint();
				}
			});

			yCategory.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					yFeatureIndex = yCategory.getSelectedIndex();
					calcRanges();
					canvas.repaint();
				}
			});
			add(canvas, BorderLayout.CENTER);
			add(controlPanel, BorderLayout.SOUTH);

			setTitle("#" + elements.size() + " - Node-ID: " + id);
			setSize(800, 800);
			setVisible(true);
		}

		private void calcRanges() {

			smallestX = 0;
			largestX = 0;
			smallestY = 0;
			largestY = 0;
			rangeX = 0;
			rangeY = 0;
			double midX = 0, midY = 0;
			if(!type.equals(Double.class)) return;
			for (Element<Double> e : elements) {
				double xVal = e.getAttribute(xFeatureIndex);
				double yVal = e.getAttribute(yFeatureIndex);
				smallestX = smallestX > xVal ? xVal : smallestX;
				smallestY = smallestY > yVal ? yVal : smallestY;
				largestX = largestX > xVal ? largestX : xVal;
				largestY = largestY > yVal ? largestY : yVal;
			}

			rangeX = largestX - smallestX;
			rangeY = largestY - smallestY;

			midX = (smallestX + largestX) / 2;
			midY = (smallestY + largestY) / 2;

			largestX += rangeX / 10;
			largestY += rangeY / 10;
			smallestX -= rangeX / 10;
			smallestY -= rangeY / 10;

			rangeX = largestX - smallestX;
			rangeY = largestY - smallestY;

			midX = (smallestX + largestX) / 2;
			midY = (smallestY + largestY) / 2;
		}
	}

	class SplitFinder extends JDialog {
		private RTree.RNode rNode;
		private JTree splitTree;
		private SplitResult[] allResults;

		public SplitFinder(RTree.RNode n) {
			rNode = n;
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Splits");
			allResults = n.getSample().calcAllSplits();

			for (SplitResult<Double> sr : allResults) {
				DefaultMutableTreeNode srNode = new DefaultMutableTreeNode("["
						+ sr.getSplitPoint().getFeatureIndex() + "] -> "
						+ sr.getSplitPoint().getSplitValue() + " => " + sr.getImpurity());

				DefaultMutableTreeNode leftDataNode = new DefaultMutableTreeNode("LEFT ["
						+ sr.getLeftSet().getElements().size() + "]");
				for (Element<Double> e : sr.getLeftSet().getElements())
					leftDataNode.add(new DefaultMutableTreeNode(e));

				DefaultMutableTreeNode rightDataNode = new DefaultMutableTreeNode(
						"RIGHT [" + sr.getRightSet().getElements().size() + "]");
				for (Element<Double> e : sr.getRightSet().getElements())
					rightDataNode.add(new DefaultMutableTreeNode(e));

				srNode.add(leftDataNode);
				srNode.add(rightDataNode);

				root.add(srNode);
			}

			tree = new JTree(root);

			add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

			setSize(640, 480);
			setTitle(n.nodeID + " - All Splits");
			setVisible(true);

		}
	}

}
