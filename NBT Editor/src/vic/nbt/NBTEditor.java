package vic.nbt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTConstants;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.NBTUtils;
import org.jnbt.Tag;

import com.ning.compress.gzip.GZIPException;

public class NBTEditor 
{
	public static JFrame frame;
	public static JMenuBar menuBar;
	
	public static JMenuItem itemNew, itemOpen, itemReload, itemSave, itemSaveAs, itemQuit, itemCut, itemCopy, itemPaste, itemDelete, itemRename, itemEdit, itemMoveUp, itemMoveDown, itemFind, itemFindNext, itemAbout;
	public static JToolBar toolBar;
	public static JButton buttonNew, buttonOpen, buttonSave, buttonRename, buttonEdit, buttonDelete, buttonTagByte, buttonTagShort, buttonTagInt, buttonTagLong, buttonTagFloat, buttonTagDouble, buttonTagByteArray, buttonTagIntArray, buttonTagString, buttonTagList, buttonTagCompound;
	
	public static JPopupMenu menuTagList;
	public static JMenuItem itemTagByte, itemTagShort, itemTagInt, itemTagLong, itemTagFloat, itemTagDouble, itemTagByteArray, itemTagIntArray, itemTagString, itemTagList, itemTagCompound;
	
	public static JTree nbtTree;
	public static TagNodeBase copy;
	
	public static File file;
	public static boolean modified = false;
	
	public static void main(String[] args) 
	{
		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setSize(500, 400);
		
		menuBar = new JMenuBar();
		menuBar.setAutoscrolls(true);
		
		itemNew = new JMenuItem("New", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/page.png")));
		itemOpen = new JMenuItem("Open...", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/folder_page.png")));
		itemReload = new JMenuItem("Reload", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrow_rotate_clockwise.png")));
		itemSave = new JMenuItem("Save...", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/disk.png")));
		itemSaveAs = new JMenuItem("Save as...", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/disk_go.png")));
		itemQuit = new JMenuItem("Quit", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/door.png")));
		itemCut = new JMenuItem("Cut", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/cut.png")));
		itemCopy = new JMenuItem("Copy", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/page_white_copy.png")));
		itemPaste = new JMenuItem("Paste", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/page_paste.png")));
		itemDelete = new JMenuItem("Delete", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/cross.png")));
		itemRename = new JMenuItem("Rename", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/textfield_rename.png")));
		itemEdit = new JMenuItem("Edit...", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/pencil.png")));
		itemMoveUp = new JMenuItem("Move up", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrow_up.png")));
		itemMoveDown = new JMenuItem("Move down", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrow_down.png")));
		itemFind = new JMenuItem("Find...", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/magnifier.png")));
		itemFindNext = new JMenuItem("Find Next", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/magnifier_go.png")));
		itemAbout = new JMenuItem("About", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/information.png")));
		
		itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		itemReload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
		itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		itemEdit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		itemMoveUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK));
		itemMoveDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK));
		itemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		itemFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		itemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		
		itemNew.addActionListener(EventListener.instance);
		itemOpen.addActionListener(EventListener.instance);
		itemReload.addActionListener(EventListener.instance);
		itemSave.addActionListener(EventListener.instance);
		itemSaveAs.addActionListener(EventListener.instance);
		itemQuit.addActionListener(EventListener.instance);
		itemCut.addActionListener(EventListener.instance);
		itemCopy.addActionListener(EventListener.instance);
		itemPaste.addActionListener(EventListener.instance);
		itemDelete.addActionListener(EventListener.instance);
		itemRename.addActionListener(EventListener.instance);
		itemEdit.addActionListener(EventListener.instance);
		itemMoveUp.addActionListener(EventListener.instance);
		itemMoveDown.addActionListener(EventListener.instance);
		itemFind.addActionListener(EventListener.instance);
		itemFindNext.addActionListener(EventListener.instance);
		itemAbout.addActionListener(EventListener.instance);
		
		//I have no idea what these are used for. If you have an idea what these do in the original NBTEdit then tell me.
		itemMoveUp.setEnabled(false);
		itemMoveDown.setEnabled(false);
		
		JMenu menuFile = new JMenu("File");
		JMenu menuEdit = new JMenu("Edit");
		JMenu menuSearch = new JMenu("Search");
		JMenu menuHelp = new JMenu("Help");
		
		menuFile.add(itemNew);
		menuFile.add(itemOpen);
		menuFile.add(itemReload);
		menuFile.addSeparator();
		menuFile.add(itemSave);
		menuFile.add(itemSaveAs);
		menuFile.addSeparator();
		menuFile.add(itemQuit);
		
		menuEdit.add(itemCut);
		menuEdit.add(itemCopy);
		menuEdit.add(itemPaste);
		menuEdit.add(itemDelete);
		menuEdit.addSeparator();
		menuEdit.add(itemRename);
		menuEdit.add(itemEdit);
		menuEdit.addSeparator();
		menuEdit.add(itemMoveUp);
		menuEdit.add(itemMoveDown);
		
		menuSearch.add(itemFind);
		menuSearch.add(itemFindNext);
		
		menuHelp.add(itemAbout);
		
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuSearch);
		menuBar.add(menuHelp);
		
		buttonNew = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/page.png")));
		buttonOpen = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/folder_page.png")));
		buttonSave = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/disk.png"))); 
		buttonRename = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/textfield_rename.png"))); 
		buttonEdit = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/pencil.png"))); 
		buttonDelete = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/cross.png")));
		buttonTagByte = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_black.png")));
		buttonTagShort = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_red.png")));
		buttonTagInt = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_orange.png")));
		buttonTagLong = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_yellow.png")));
		buttonTagFloat = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_pink.png")));
		buttonTagDouble = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_purple.png")));
		buttonTagByteArray = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrows_black.png")));
		buttonTagIntArray = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrows_orange.png")));
		buttonTagString = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/text_smallcaps.png")));
		buttonTagList = new JButton("\u25BE", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/note.png")));
		buttonTagCompound = new JButton(new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/package.png")));
		
		buttonNew.addActionListener(EventListener.instance);
		buttonOpen.addActionListener(EventListener.instance);
		buttonSave.addActionListener(EventListener.instance);
		buttonRename.addActionListener(EventListener.instance);
		buttonEdit.addActionListener(EventListener.instance);
		buttonDelete.addActionListener(EventListener.instance);
		buttonTagByte.addActionListener(EventListener.instance);
		buttonTagShort.addActionListener(EventListener.instance);
		buttonTagInt.addActionListener(EventListener.instance);
		buttonTagLong.addActionListener(EventListener.instance);
		buttonTagFloat.addActionListener(EventListener.instance);
		buttonTagDouble.addActionListener(EventListener.instance);
		buttonTagByteArray.addActionListener(EventListener.instance);
		buttonTagIntArray.addActionListener(EventListener.instance);
		buttonTagString.addActionListener(EventListener.instance);
		buttonTagCompound.addActionListener(EventListener.instance);
		
		buttonNew.setToolTipText("New (" + Utils.toString(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)) + ")");
		buttonOpen.setToolTipText("Open... (" + Utils.toString(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)) + ")");
		buttonSave.setToolTipText("Save... (" + Utils.toString(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)) + ")");
		buttonRename.setToolTipText("Rename");
		buttonEdit.setToolTipText("Edit... (" + Utils.toString(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)) + ")");
		buttonDelete.setToolTipText("Delete (" + Utils.toString(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)) + ")");
		buttonTagByte.setToolTipText("Byte");
		buttonTagShort.setToolTipText("Short");
		buttonTagInt.setToolTipText("Int");
		buttonTagLong.setToolTipText("Long");
		buttonTagFloat.setToolTipText("Float");
		buttonTagDouble.setToolTipText("Double");
		buttonTagByteArray.setToolTipText("ByteArray");
		buttonTagIntArray.setToolTipText("IntArray");
		buttonTagString.setToolTipText("String");
		buttonTagList.setToolTipText("List");
		buttonTagCompound.setToolTipText("Compound");
		
		buttonTagList.addActionListener(new ActionListener() 
		{		
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				menuTagList.show(buttonTagList, 0, 0 + buttonTagList.getHeight());
			}
		});
		
		//Sorry but KeyEvent.VK_CIRCUMFLEX doesn't work. At least not on a german keyboard ;)
		/*buttonTagByte.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_CIRCUMFLEX, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagShort.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagInt.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagLong.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagFloat.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagDouble.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagByteArray.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagIntArray.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagString.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.CTRL_DOWN_MASK), "keyStroke");
		buttonTagCompound.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.CTRL_DOWN_MASK), "keyStroke");*/

		menuTagList = new JPopupMenu();
		
		itemTagByte = new JMenuItem("Byte", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_black.png")));
		itemTagShort = new JMenuItem("Short", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_red.png")));
		itemTagInt = new JMenuItem("Int", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_orange.png")));
		itemTagLong = new JMenuItem("Long", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_yellow.png")));
		itemTagFloat = new JMenuItem("Float", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_pink.png")));
		itemTagDouble = new JMenuItem("Double", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/bullet_purple.png")));
		itemTagByteArray = new JMenuItem("ByteArray", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrows_black.png")));
		itemTagIntArray = new JMenuItem("IntArray", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/arrows_orange.png")));
		itemTagString = new JMenuItem("String", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/text_smallcaps.png")));
		itemTagList = new JMenuItem("List", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/note.png")));
		itemTagCompound = new JMenuItem("Compound", new ImageIcon(Utils.getImageFromJar("/vic/nbt/resources/package.png")));
		
		itemTagByte.addActionListener(EventListener.instance);
		itemTagShort.addActionListener(EventListener.instance);
		itemTagInt.addActionListener(EventListener.instance);
		itemTagLong.addActionListener(EventListener.instance);
		itemTagFloat.addActionListener(EventListener.instance);
		itemTagDouble.addActionListener(EventListener.instance);
		itemTagByteArray.addActionListener(EventListener.instance);
		itemTagIntArray.addActionListener(EventListener.instance);
		itemTagString.addActionListener(EventListener.instance);
		itemTagList.addActionListener(EventListener.instance);
		itemTagCompound.addActionListener(EventListener.instance);
		
		/*itemTagByte.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CIRCUMFLEX, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagShort.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagInt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagLong.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagFloat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagDouble.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagByteArray.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagIntArray.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagString.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		itemTagCompound.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));*/
		
		menuTagList.add(itemTagByte);
		menuTagList.add(itemTagShort);
		menuTagList.add(itemTagInt);
		menuTagList.add(itemTagLong);
		menuTagList.add(itemTagFloat);
		menuTagList.add(itemTagDouble);
		menuTagList.add(itemTagByteArray);
		menuTagList.add(itemTagIntArray);
		menuTagList.add(itemTagString);
		menuTagList.add(itemTagList);
		menuTagList.add(itemTagCompound);
		
		toolBar = new JToolBar();
		
		toolBar.add(buttonNew);
		toolBar.add(buttonOpen);
		toolBar.add(buttonSave);
		toolBar.addSeparator();
		toolBar.add(buttonRename);
		toolBar.add(buttonEdit);
		toolBar.add(buttonDelete);
		toolBar.addSeparator();
		toolBar.add(buttonTagByte);
		toolBar.add(buttonTagShort); 
		toolBar.add(buttonTagInt);
		toolBar.add(buttonTagLong);
		toolBar.add(buttonTagFloat);
		toolBar.add(buttonTagDouble);
		toolBar.add(buttonTagByteArray);
		toolBar.add(buttonTagIntArray);
		toolBar.add(buttonTagString);
		toolBar.add(buttonTagList);
		toolBar.add(buttonTagCompound);
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(toolBar, BorderLayout.PAGE_START);
		
		nbtTree = new JTree(new CompoundTagNode("root"));
		//You don't have to tell me that this is ugly.
		nbtTree.getInputMap().getParent().remove(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		nbtTree.getInputMap().getParent().remove(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		nbtTree.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		nbtTree.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		nbtTree.setSelectionPath(new TreePath(nbtTree.getModel().getRoot()));
		nbtTree.setCellRenderer(new TagNodeRenderer());
		nbtTree.setCellEditor(new TagNodeCellEditor(nbtTree, nbtTree.getCellRenderer()));
		nbtTree.addTreeSelectionListener(new TreeSelectionListener() 
		{	
			@Override
			public void valueChanged(TreeSelectionEvent e) 
			{
				updateUI();
			}
		});
		nbtTree.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount() > 1)
				{
					editCell();
				}
			}
		});
		nbtTree.setEditable(true);
		updateUI();
		
		panel1.add(new JScrollPane(nbtTree), BorderLayout.CENTER);
		frame.add(menuBar, BorderLayout.PAGE_START);
		frame.add(panel1, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		updateName();
		frame.setVisible(true);
	}
	
	public static void editCell()
	{
		if(!(nbtTree.getLastSelectedPathComponent() instanceof TagLeaf)) return;
		final TagLeaf node = (TagLeaf) nbtTree.getLastSelectedPathComponent();
		
		final JDialog edit = new JDialog(frame);
		edit.setTitle("Edit value... (" + node.getName() + ")");
		edit.setModal(true);
		
		final JTextField field = new JTextField(node.getValue());
		field.setSelectionStart(0);
		field.setSelectionEnd(field.getText().length());
		field.setPreferredSize(new Dimension(250, 25));
		field.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try {
					node.setValue(field.getText());
					((DefaultTreeModel)nbtTree.getModel()).nodeChanged(node);
					edit.dispose();
					modified = true;
					updateName();
				} catch (Exception e2) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		});
		edit.add(field);
		
		edit.pack();
		edit.setLocationRelativeTo(frame);
		edit.setVisible(true);
	}
	
	public static void copy()
	{
		copy = ((TagNodeBase)nbtTree.getLastSelectedPathComponent()).clone();
	}
	
	public static void paste()
	{
		TagNode node = (TagNode)nbtTree.getLastSelectedPathComponent();
		TagNodeBase copy = NBTEditor.copy.clone();
		
		copy.setParent(null);
		if(node.containsTagWithName(copy.getName()))
		{
			copy.setName(Utils.getNextFreeName(node));
		}	
		
		TreePath path = new TreePath(((DefaultTreeModel)NBTEditor.nbtTree.getModel()).getPathToRoot(node));
		node.addNode(copy);
		((DefaultTreeModel)NBTEditor.nbtTree.getModel()).nodeChanged(node);
		((DefaultTreeModel)NBTEditor.nbtTree.getModel()).nodesWereInserted(node, new int[]{node.getIndex(copy)});
		if(!NBTEditor.nbtTree.isExpanded(path)) NBTEditor.nbtTree.expandPath(path);
		
		NBTEditor.modified = true;
		NBTEditor.updateName();
	}
	
	public static void open()
	{
		if(modified)
		{
			int result = JOptionPane.showConfirmDialog(frame, "File has been modified. Save file?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
			if(result == JOptionPane.YES_OPTION)
			{
				save();
			}
			else if(result == JOptionPane.CANCEL_OPTION) return;
		}
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("NamedBinaryTag (*.nbt)", "nbt");
		chooser.addChoosableFileFilter(filter);
		chooser.setDialogTitle("Open...");
		chooser.setVisible(true); 

		int result = chooser.showOpenDialog(frame);

		if(result == JFileChooser.APPROVE_OPTION) 
		{
			file = chooser.getSelectedFile();
			reload();
		}
	}
	
	public static void newFile()
	{
		if(modified)
		{
			int result = JOptionPane.showConfirmDialog(frame, "File has been modified. Save file?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
			if(result == JOptionPane.YES_OPTION)
			{
				save();
			}
			else if(result == JOptionPane.CANCEL_OPTION) return;
		}
		
		file = null;
		modified = false;
		((DefaultTreeModel)nbtTree.getModel()).setRoot(new CompoundTagNode("root"));
		updateName();
		updateUI();
	}
	
	private static TagNodeBase generateNode(Tag tag)
	{
		if(tag instanceof CompoundTag)
		{
			CompoundTagNode node = new CompoundTagNode(tag.getName());
			for(Tag tag2 : ((CompoundTag)tag).getValue().values())
			{
				node.addNode(generateNode(tag2));			
			}
			node.sortNodes();
			return node;
		}
		else if(tag instanceof ListTag)
		{
			ListTagNode node = new ListTagNode(tag.getName(), NBTUtils.getTypeCode(((ListTag)tag).getType()));
			for(Tag tag2 : ((ListTag)tag).getValue())
			{
				node.addNode(generateNode(tag2));
			}
			return node;
		}
		else
		{
			TagLeaf node = new TagLeaf(tag.getName(), NBTUtils.getTypeCode(tag.getClass()), tag.getValue());	
			return node;
		}
	}
	
	public static void reload()
	{
		if(file == null) return;
		try {
			NBTInputStream is = new NBTInputStream(new FileInputStream(file));
			CompoundTag tag = (CompoundTag)is.readTag();
			CompoundTagNode node = (CompoundTagNode)generateNode(tag);
			node.sortNodes();
			((DefaultTreeModel)nbtTree.getModel()).setRoot(node);
			updateName();
			updateUI();
			is.close();
		} catch (GZIPException e) {
			file = null;
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Error while opening file " + file.getName() + ":\nGZIPException You tried to open a file that is not in GZIP format.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			file = null;
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Error while opening file " + file.getName() + ":\nIOException Maybe the file is missing or damadged.", "Error", JOptionPane.ERROR_MESSAGE);
		} 
	}
	
	public static void save()
	{
		if(file == null) 
		{
			saveAs();
			return;
		}
		
		try {
			NBTOutputStream os = new NBTOutputStream(new FileOutputStream(file));
			os.writeTag(((TagNodeBase)nbtTree.getModel().getRoot()).toNBTTag());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		modified = false;
		updateName();
	}
	
	public static void saveAs()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(new File("unnamed.nbt"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("NamedBinaryTag (*.nbt)", "nbt");
		chooser.addChoosableFileFilter(filter);
		chooser.setDialogTitle("Save As...");
		chooser.setVisible(true); 

		int result = chooser.showSaveDialog(frame);

		if(result == JFileChooser.APPROVE_OPTION) 
		{
			file = chooser.getSelectedFile();
			
			String extension = Utils.getExtension(file);
			if((!extension.equals("nbt") && chooser.getFileFilter() == filter) || extension.length() == 0) file = new File(file.getName() + ".nbt");
			
			if(file.exists())
			{
				int result2 = JOptionPane.showConfirmDialog(frame, file.getName() + " already exists.\nDo you want to replace it?", "Confirm Save As...", JOptionPane.YES_NO_OPTION);
				if(result2 == JFileChooser.CANCEL_OPTION) return;
			}
			save();
		}
	}
	
	public static void updateName()
	{
		frame.setTitle("NBT Editor - " + (file != null ? file.getName() : "unnamed.nbt") + (modified ? "*" : ""));
	}
	
	public static void updateUI()
	{
		//I should really optimize all these by adding the type directly to the button / item.
		TagNodeBase node = (TagNodeBase)nbtTree.getLastSelectedPathComponent();
		if(node == null) 
		{
			node = (TagNodeBase)nbtTree.getModel().getRoot();
			nbtTree.setSelectionPath(new TreePath(nbtTree.getModel().getRoot()));
		}
		
		if(file == null) itemReload.setEnabled(false);
		else itemReload.setEnabled(true);
		
		if(node instanceof TagNode && copy != null) itemPaste.setEnabled(true);
		else itemPaste.setEnabled(false);
		
		if(node.getParent() == null) 
		{
			buttonDelete.setEnabled(false);
			itemDelete.setEnabled(false);
			itemCut.setEnabled(false);
		}
		else 
		{
			buttonDelete.setEnabled(true);
			itemDelete.setEnabled(true);
			itemCut.setEnabled(false);
		}
		
		if(node instanceof TagLeaf)
		{
			buttonEdit.setEnabled(true);
			itemEdit.setEnabled(true);
		}
		else
		{
			buttonEdit.setEnabled(false);
			itemEdit.setEnabled(false);
		}
		
		if(node.getParent() != null && node.getParent() instanceof ListTagNode) 
		{
			buttonRename.setEnabled(false);
			itemRename.setEnabled(false);
		}
		else
		{
			buttonRename.setEnabled(true);
			itemRename.setEnabled(true);
		}
		
		buttonTagByte.setEnabled(true);
		buttonTagShort.setEnabled(true);
		buttonTagInt.setEnabled(true);
		buttonTagLong.setEnabled(true);
		buttonTagFloat.setEnabled(true);
		buttonTagDouble.setEnabled(true);
		buttonTagByteArray.setEnabled(true);
		buttonTagIntArray.setEnabled(true);
		buttonTagString.setEnabled(true);
		buttonTagList.setEnabled(true);
		buttonTagCompound.setEnabled(true);
		
		if(node instanceof TagLeaf || node instanceof ListTagNode)
		{
			buttonTagByte.setEnabled(false);
			buttonTagShort.setEnabled(false);
			buttonTagInt.setEnabled(false);
			buttonTagLong.setEnabled(false);
			buttonTagFloat.setEnabled(false);
			buttonTagDouble.setEnabled(false);
			buttonTagByteArray.setEnabled(false);
			buttonTagIntArray.setEnabled(false);
			buttonTagString.setEnabled(false);
			buttonTagList.setEnabled(false);
			buttonTagCompound.setEnabled(false);
			
			if(node instanceof ListTagNode)
			{
				switch(((ListTagNode)node).getTagType())
				{
				case NBTConstants.TYPE_BYTE: buttonTagByte.setEnabled(true); break;
				case NBTConstants.TYPE_SHORT: buttonTagShort.setEnabled(true); break;
				case NBTConstants.TYPE_INT: buttonTagInt.setEnabled(true); break;
				case NBTConstants.TYPE_LONG: buttonTagLong.setEnabled(true); break;
				case NBTConstants.TYPE_FLOAT: buttonTagFloat.setEnabled(true); break;
				case NBTConstants.TYPE_DOUBLE: buttonTagDouble.setEnabled(true); break;
				case NBTConstants.TYPE_BYTE_ARRAY: buttonTagByteArray.setEnabled(true); break;
				case NBTConstants.TYPE_INT_ARRAY: buttonTagIntArray.setEnabled(true); break;
				case NBTConstants.TYPE_STRING: buttonTagString.setEnabled(true); break;
				case NBTConstants.TYPE_LIST: buttonTagList.setEnabled(true); break;
				case NBTConstants.TYPE_COMPOUND: buttonTagCompound.setEnabled(true); break;
				}
			}
		}
	}
}
