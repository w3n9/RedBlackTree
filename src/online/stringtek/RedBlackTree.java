package online.stringtek;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class RedBlackTree<K extends Comparable<K>,V> {
    enum Color{
        RED,BLACK
    }
    private Node<K,V> root;
    private static class Node<K,V>{
        Color color;
        Node<K,V> father,left,right;
        K key;
        V value;
        public Node(){ }
        public Node(K key, V value,Color color) {
            this.key = key;
            this.value = value;
            this.color=color;
        }

        @Override
        public String toString() {
            return "Node{"+(this.father==null?"":this.father.key)+","+this.key+","+this.color+"}";
        }
    }
    private boolean isBlack(Node<K,V> node){
        return node==null||node.color==Color.BLACK;
    }
    private boolean isRed(Node<K,V> node){
        return !isBlack(node);
    }
    /**
     * @param father   父节点
     * @param node     待插入节点
     * @return node是否应该是father的左节点
     * */
    private boolean isLeft(Node<K,V> father,Node<K,V> node){
        return node.key.compareTo(father.key)<0;
    }
    /**
     * @param node 当前节点
     * @return 当前节点的兄弟节点，可为null，配合isBlack或者isRed使用没有问题
     * */
    private Node<K,V> getBrother(Node<K,V> node){
        Node<K,V> father=node.father;
        return father.left==node?father.right:father.left;
    }
    /**
     * 红黑树添加操作内部方法
     * @param node 要添加的红黑树节点
     * */
    private void addNode(Node<K,V> node){
        if(root==null){//当前树为空
            root=node;
            node.color=Color.BLACK;
        }else{
            Node<K,V> ptr=root,prev=null;
            while(ptr!=null){
                int ans=node.key.compareTo(ptr.key);
                if(ans<0){
                    prev=ptr;
                    ptr=ptr.left;
                }else if(ans>0){
                    prev=ptr;
                    ptr=ptr.right;
                }else{//树中已经存在这个key，则只需要进行更新即可
                    ptr.value=node.value;
                    return;
                }
            }
            //插入操作
            if(isLeft(prev,node)){
                prev.left=node;
            }else{
                prev.right=node;
            }
            node.father=prev;
            //fix整个树
            fix(node);
        }
    }
    private void fix(Node<K,V> node){
        if(node==this.root){//已经在最顶层此时只需确保根节点的颜色是黑色
            node.color=Color.BLACK;
            return;
        }
        if(node==null){
            return;
        }
        Node<K,V> prev=node.father;
        //树中不存在key，需要插入
        if(isRed(prev)){//父节点是红色
            Node<K,V> prevBro=getBrother(prev);
            if(isRed(prevBro)){//父节点的兄弟节点是红色
                //此时仅需变色
                prev.color=Color.BLACK;
                prev.father.color=Color.RED;
                prevBro.color=Color.BLACK;
                //并且将祖先节点设为当前节点
                fix(prev.father);
            }else{//父节点的兄弟节点是黑色
                if(isLeft(prev.father,prev)){//父节点是祖父节点的左节点
                    if(isLeft(prev,node)){//是父节点的左节点
                        //LL双红
                        prev.father.color=Color.RED;
                        prev.color=Color.BLACK;
                        rightRotate(prev.father);
                    }else{
                        //LR双红
                        leftRotate(prev);

                        prev.father.color=Color.BLACK;
                        prev.father.father.color=Color.RED;

                        rightRotate(prev.father.father);
                    }
                }else{//是父节点的右节点
                    if(isLeft(prev,node)){//是父节点的左节点
                        //RL双红
                        rightRotate(prev);

                        prev.father.color=Color.BLACK;
                        prev.father.father.color=Color.RED;

                        leftRotate(prev.father.father);
                    }else{
                        //RR双红
                        prev.father.color=Color.RED;
                        prev.color=Color.BLACK;
                        leftRotate(prev.father);
                    }
                }
            }
        }
        //父节点是黑色，插入不会影响平衡，所以可以直接插入无需处理
    }
    /**
     * 以node节点为基准进行右旋
     * @param node 要进行旋转的节点
     * */
    private void rightRotate(Node<K,V> node){
        Node<K,V> left=node.left,father=node.father,leftRight=left.right;
        //左节点与当前节点更改关系
        left.right=node;
        node.father=left;
        //==================
        //左节点的右子树当前节点建立双向联系
        node.left=leftRight;
        if(leftRight!=null){
            leftRight.father=node;
        }
        //==================
        if(father==null){
            this.root=left;
            this.root.father=null;
        }else{
            left.father=father;
            if(isLeft(father,left)){
                father.left=left;
            }else{
                father.right=left;
            }
        }
    }
    /**
     * 以node节点为基准进行左旋
     * @param node 要进行旋转的节点
     * */
    private void leftRotate(Node<K,V> node){
        Node<K,V> right=node.right,father=node.father,rightLeft=right.left;
        //右节点与当前节点更改关系
        right.left=node;
        node.father=right;
        //右节点的左子树挂到node的右边
        node.right=rightLeft;
        if(rightLeft!=null){
            rightLeft.father=node;
        }
        if(father==null){
            this.root=right;
            this.root.father=null;
        }else{
            right.father=father;
            if(isLeft(father,right)){
                father.left=right;
            }else{
                father.right=right;
            }
        }
    }
    /**
     * @param key 键
     * @return 返回key对应的Node节点，不存在则返回null
     * */
    private Node<K,V> getNode(K key){
        Node<K,V> ptr=root;
        while(ptr!=null){
            int ans=key.compareTo(ptr.key);
            if(ans<0){
                ptr=ptr.left;
            }else if(ans==0){
                return ptr;
            }else{
                ptr=ptr.right;
            }
        }
        return null;
    }





    private void levelTraverse(Node<K,V> node){
        Queue<Node<K,V>> queue=new ArrayDeque<>();
        queue.offer(node);
        while(!queue.isEmpty()){
            Node<K,V> cur=queue.poll();
            //输出cur
            System.out.println(cur);
            //然后将cur的左右子节点放入队列
            if(cur.left!=null){
                queue.offer(cur.left);
            }
            if(cur.right!=null){
                queue.offer(cur.right);
            }
        }
    }




    private void inOrderTraverse(Node<K,V> node){
        if(node!=null){
            inOrderTraverse(node.left);
            System.out.println(node.key+"=>"+node.value);
            inOrderTraverse(node.right);
        }
    }

    /**
     * 红黑树添加操作对外方法
     * @param key    键
     * @param value  值
     * */
    public void put(K key,V value){
        Node<K,V> node=new Node<>(key,value,Color.RED);
        this.addNode(node);
    }
    /**
     * 红黑树查询操作对外方法
     * @param key    键
     * @return 键对应的值，如果键不存在则为null
     * */
    public V get(K key){
        Node<K,V> ans=this.getNode(key);
        return ans==null?null:ans.value;
    }

    public void inOrderTraverse(){
        if(this.root!=null){
            this.inOrderTraverse(this.root);
        }
    }
    public void levelTraverse(){
        if(this.root!=null){
            this.levelTraverse(this.root);
        }
    }
    public static void main(String[] args) {
        RedBlackTree<Integer,String> tree=new RedBlackTree<>();
        tree.put(1,"123");
        tree.put(9,"aosdjao");
        tree.put(5,"kkp");
        tree.put(3,"sb");
        tree.put(-1,"asdjaosd");
        tree.put(7,"asdasd");
        tree.put(8,"fuck");
        tree.inOrderTraverse();
        System.out.println("===================");
        tree.levelTraverse();
    }
}
