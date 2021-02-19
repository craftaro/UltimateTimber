package com.songoda.ultimatetimber.tree;

import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeBlockSet<BlockType> implements Collection {

    private final ITreeBlock<BlockType> initialLogBlock;
    private List<ITreeBlock<BlockType>> logBlocks;
    private final List<ITreeBlock<BlockType>> leafBlocks;

    public TreeBlockSet() {
        this.initialLogBlock = null;
        this.logBlocks = new LinkedList<>();
        this.leafBlocks = new LinkedList<>();
    }

    public TreeBlockSet(ITreeBlock<BlockType> initialLogBlock) {
        this.initialLogBlock = initialLogBlock;
        this.logBlocks = new LinkedList<>();
        this.leafBlocks = new LinkedList<>();

        if (initialLogBlock != null)
            this.logBlocks.add(initialLogBlock);
    }

    /**
     * Gets the TreeBlock that initiated the tree topple
     *
     * @return The TreeBlock of the initial topple point
     */
    public ITreeBlock<BlockType> getInitialLogBlock() {
        return this.initialLogBlock;
    }

    /**
     * Gets all logs in this TreeBlockSet
     *
     * @return A Set of TreeBlocks
     */
    public List<ITreeBlock<BlockType>> getLogBlocks() {
        return Collections.unmodifiableList(this.logBlocks);
    }

    /**
     * Gets all leaves in this TreeBlockSet
     *
     * @return A Set of TreeBlocks
     */
    public List<ITreeBlock<BlockType>> getLeafBlocks() {
        return Collections.unmodifiableList(this.leafBlocks);
    }

    /**
     * Gets all blocks in this TreeBlockSet
     *
     * @return A Set of all TreeBlocks
     */
    public Set<ITreeBlock<BlockType>> getAllTreeBlocks() {
        Set<ITreeBlock<BlockType>> treeBlocks = new HashSet<>();
        treeBlocks.addAll(this.logBlocks);
        treeBlocks.addAll(this.leafBlocks);
        return treeBlocks;
    }

    @Override
    public int size() {
        return this.logBlocks.size() + this.leafBlocks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.logBlocks.isEmpty() && this.leafBlocks.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.logBlocks.contains(o) || this.leafBlocks.contains(o);
    }

    @Override
    public Iterator iterator() {
        return this.getAllTreeBlocks().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.getAllTreeBlocks().toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(Object o) {
        if (!(o instanceof ITreeBlock)) return false;
        ITreeBlock treeBlock = (ITreeBlock) o;
        switch (treeBlock.getTreeBlockType()) {
            case LOG:
                return this.logBlocks.add(treeBlock);
            case LEAF:
                return this.leafBlocks.add(treeBlock);
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof ITreeBlock)) return false;
        ITreeBlock treeBlock = (ITreeBlock) o;
        switch (treeBlock.getTreeBlockType()) {
            case LOG:
                return this.logBlocks.remove(treeBlock);
            case LEAF:
                return this.leafBlocks.remove(treeBlock);
        }
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        boolean allAdded = true;
        for (Object o : c) {
            if (!this.add(o)) {
                allAdded = false;
            }
        }
        return allAdded;
    }

    @Override
    public void clear() {
        this.logBlocks.clear();
        this.leafBlocks.clear();
    }

    @Override
    public boolean retainAll(Collection c) {
        boolean retainedAll = true;
        for (Object o : c) {
            if (!this.contains(o)) {
                this.remove(o);
            } else {
                retainedAll = false;
            }
        }
        return retainedAll;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean removedAll = true;
        for (Object o : c) {
            if (this.contains(o)) {
                this.remove(o);
            } else {
                removedAll = false;
            }
        }
        return removedAll;
    }

    public void sortAndLimit(int max) {
        if (logBlocks.size() < max)
            return;

        logBlocks = logBlocks.stream().sorted(Comparator.comparingInt(b -> b.getLocation().getBlockY()))
                .limit(max).collect(Collectors.toList());

        int highest = logBlocks.get(logBlocks.size() - 1).getLocation().getBlockY();

        if (logBlocks.size() >= max)
            for (ITreeBlock<BlockType> leafBlock : new LinkedList<>(leafBlocks))
                if (leafBlock.getLocation().getY() > highest)
                    leafBlocks.remove(leafBlock);
    }

    /**
     * Removes all tree blocks of a given type
     *
     * @param treeBlockType The type of tree block to remove
     * @return If any blocks were removed
     */
    public boolean removeAll(TreeBlockType treeBlockType) {
        if (treeBlockType.equals(TreeBlockType.LOG)) {
            boolean removedAny = !this.logBlocks.isEmpty();
            this.logBlocks.clear();
            return removedAny;
        } else if (treeBlockType.equals(TreeBlockType.LEAF)) {
            boolean removedAny = !this.leafBlocks.isEmpty();
            this.leafBlocks.clear();
            return removedAny;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object o : c)
            if (!this.contains(o))
                return false;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray(Object[] a) {
        Set<ITreeBlock<BlockType>> treeBlocks = new HashSet<>();
        for (Object o : a)
            if (o instanceof ITreeBlock)
                treeBlocks.add((ITreeBlock<BlockType>)o);
        return treeBlocks.toArray();
    }

}
