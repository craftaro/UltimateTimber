package com.songoda.ultimatetimber.tree;

import java.util.*;

public class TreeBlockSet implements Collection {

    private Set<TreeBlock> logBlocks;
    private Set<TreeBlock> leafBlocks;

    public TreeBlockSet() {
        this.logBlocks = new HashSet<>();
        this.leafBlocks = new HashSet<>();
    }

    /**
     * Gets all logs in this TreeBlockSet
     *
     * @return A Set of TreeBlocks
     */
    public Set<TreeBlock> getLogBlocks() {
        return Collections.unmodifiableSet(this.logBlocks);
    }

    /**
     * Gets all leaves in this TreeBlockSet
     *
     * @return A Set of TreeBlocks
     */
    public Set<TreeBlock> getLeafBlocks() {
        return Collections.unmodifiableSet(this.leafBlocks);
    }

    /**
     * Gets all blocks in this TreeBlockSet
     *
     * @return A Set of all TreeBlocks
     */
    public Set<TreeBlock> getAllTreeBlocks() {
        Set<TreeBlock> treeBlocks = new HashSet<>();
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
    public boolean add(Object o) {
        if (!(o instanceof TreeBlock)) return false;
        TreeBlock treeBlock = (TreeBlock) o;
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
        if (!(o instanceof TreeBlock)) return false;
        TreeBlock treeBlock = (TreeBlock) o;
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

    @Override
    public boolean containsAll(Collection c) {
        for (Object o : c)
            if (!this.contains(o))
                return false;
        return true;
    }

    @Override
    public Object[] toArray(Object[] a) {
        Set<TreeBlock> treeBlocks = new HashSet<>();
        for (Object o : a)
            if (o instanceof TreeBlock)
                treeBlocks.add((TreeBlock)o);
        return treeBlocks.toArray();
    }

}
