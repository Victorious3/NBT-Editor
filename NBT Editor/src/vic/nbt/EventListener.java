package vic.nbt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jnbt.NBTConstants;

public class EventListener implements ActionListener
{
	public static EventListener instance = new EventListener();
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//A lot of quick-and-dirty code.
		if(e.getSource() == NBTEditor.itemRename || e.getSource() == NBTEditor.buttonRename)
		{
			NBTEditor.nbtTree.startEditingAtPath(NBTEditor.nbtTree.getSelectionPath());
			NBTEditor.modified = true;
			NBTEditor.updateName();
		}	
		else if(e.getSource() == NBTEditor.itemEdit || e.getSource() == NBTEditor.buttonEdit)
		{
			NBTEditor.editCell();
		}
		else if(e.getSource() == NBTEditor.itemOpen || e.getSource() == NBTEditor.buttonOpen)
		{
			NBTEditor.open();
		}
		else if(e.getSource() == NBTEditor.itemCopy)
		{
			NBTEditor.copy();
		}
		else if(e.getSource() == NBTEditor.itemPaste)
		{
			NBTEditor.paste();
		}
		else if(e.getSource() == NBTEditor.itemReload)
		{
			NBTEditor.reload();
		}
		else if(e.getSource() == NBTEditor.itemNew || e.getSource() == NBTEditor.buttonNew)
		{
			NBTEditor.newFile();
		}
		else if(e.getSource() == NBTEditor.itemSave || e.getSource() == NBTEditor.buttonSave)
		{
			NBTEditor.save();
		}
		else if(e.getSource() == NBTEditor.itemSaveAs)
		{
			NBTEditor.saveAs();
		}
		else if(e.getSource() == NBTEditor.itemDelete|| e.getSource() == NBTEditor.buttonDelete)
		{
			TagNodeBase node = (TagNodeBase)NBTEditor.nbtTree.getLastSelectedPathComponent();
			int index = node.getParent().getIndex(node);
			((TagNode)((TagNodeBase)NBTEditor.nbtTree.getLastSelectedPathComponent()).getParent()).removeNode(node);
			((DefaultTreeModel)NBTEditor.nbtTree.getModel()).nodesWereRemoved(node.getParent(), new int[]{index}, new Object[]{node});
			if(index < node.getParent().getChildCount())
			{
				NBTEditor.nbtTree.setSelectionPath(new TreePath(((DefaultTreeModel)NBTEditor.nbtTree.getModel()).getPathToRoot(node.getParent().getChildAt(index))));
			}
			else if(index > 0)
			{
				NBTEditor.nbtTree.setSelectionPath(new TreePath(((DefaultTreeModel)NBTEditor.nbtTree.getModel()).getPathToRoot(node.getParent().getChildAt(index - 1))));
			}
			else NBTEditor.nbtTree.setSelectionPath(new TreePath(((DefaultTreeModel)NBTEditor.nbtTree.getModel()).getPathToRoot(node.getParent())));
			NBTEditor.modified = true;
			NBTEditor.updateName();
		}
		
		int type = 0;
		boolean list = false;
		
		if(e.getSource() == NBTEditor.buttonTagByte) 			type = NBTConstants.TYPE_BYTE;
		else if(e.getSource() == NBTEditor.buttonTagShort) 		type = NBTConstants.TYPE_SHORT;
		else if(e.getSource() == NBTEditor.buttonTagInt) 		type = NBTConstants.TYPE_INT;
		else if(e.getSource() == NBTEditor.buttonTagLong) 		type = NBTConstants.TYPE_LONG;
		else if(e.getSource() == NBTEditor.buttonTagFloat) 		type = NBTConstants.TYPE_FLOAT;
		else if(e.getSource() == NBTEditor.buttonTagDouble) 	type = NBTConstants.TYPE_DOUBLE;
		else if(e.getSource() == NBTEditor.buttonTagByteArray) 	type = NBTConstants.TYPE_BYTE_ARRAY;
		else if(e.getSource() == NBTEditor.buttonTagIntArray) 	type = NBTConstants.TYPE_INT_ARRAY;
		else if(e.getSource() == NBTEditor.buttonTagString) 	type = NBTConstants.TYPE_STRING;
		else if(e.getSource() == NBTEditor.buttonTagCompound) 	type = NBTConstants.TYPE_COMPOUND;
		else if(e.getSource() == NBTEditor.itemTagByte){		type = NBTConstants.TYPE_BYTE; 			list = true;}
		else if(e.getSource() == NBTEditor.itemTagShort){		type = NBTConstants.TYPE_SHORT;			list = true;}
		else if(e.getSource() == NBTEditor.itemTagInt){			type = NBTConstants.TYPE_INT;			list = true;}
		else if(e.getSource() == NBTEditor.itemTagLong){		type = NBTConstants.TYPE_LONG;			list = true;}
		else if(e.getSource() == NBTEditor.itemTagFloat){		type = NBTConstants.TYPE_FLOAT;			list = true;}
		else if(e.getSource() == NBTEditor.itemTagDouble){		type = NBTConstants.TYPE_DOUBLE;		list = true;}
		else if(e.getSource() == NBTEditor.itemTagByteArray){	type = NBTConstants.TYPE_BYTE_ARRAY;	list = true;}
		else if(e.getSource() == NBTEditor.itemTagIntArray){	type = NBTConstants.TYPE_INT_ARRAY;		list = true;}
		else if(e.getSource() == NBTEditor.itemTagString){		type = NBTConstants.TYPE_STRING;		list = true;}
		else if(e.getSource() == NBTEditor.itemTagCompound){	type = NBTConstants.TYPE_COMPOUND;		list = true;}
		
		if(type != 0)
		{
			TagNodeBase node;
			if(list)
			{
				node = new ListTagNode(Utils.getNextFreeName(((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent())), type);
			}
			else
			{
				if(type == NBTConstants.TYPE_INT_ARRAY) node = new TagLeaf(Utils.getNextFreeName(((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent())), type, new int[0]);
				else if(type == NBTConstants.TYPE_BYTE_ARRAY) node = new TagLeaf(Utils.getNextFreeName(((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent())), type, new byte[0]);
				else if(type == NBTConstants.TYPE_STRING) node = new TagLeaf(Utils.getNextFreeName(((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent())), type, "");
				else if(type == NBTConstants.TYPE_COMPOUND) node = new CompoundTagNode(Utils.getNextFreeName(((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent())));
				else 
				{
					node = new TagLeaf(Utils.getNextFreeName(((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent())), type, 0);
					try {
						//TODO This is rather inconvenient.
						((TagLeaf)node).setValue("0");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
			TagNode parent = ((TagNode)NBTEditor.nbtTree.getLastSelectedPathComponent());
			TreePath path = new TreePath(((DefaultTreeModel)NBTEditor.nbtTree.getModel()).getPathToRoot(parent));
			parent.addNode(node);
			((DefaultTreeModel)NBTEditor.nbtTree.getModel()).nodeChanged(parent);
			((DefaultTreeModel)NBTEditor.nbtTree.getModel()).nodesWereInserted(parent, new int[]{parent.getIndex(node)});
			if(!NBTEditor.nbtTree.isExpanded(path)) NBTEditor.nbtTree.expandPath(path);
			
			NBTEditor.modified = true;
			NBTEditor.updateName();
		}
	}
}
