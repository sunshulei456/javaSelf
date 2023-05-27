/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.nio.channels;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketOption;
import java.net.SocketAddress;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

/**
 * A selectable channel for stream-oriented listening sockets.
 *
 * <p> A server-socket channel is created by invoking the {@link #open() open}
 * method of this class.  It is not possible to create a channel for an arbitrary,
 * pre-existing {@link ServerSocket}. A newly-created server-socket channel is
 * open but not yet bound.  An attempt to invoke the {@link #accept() accept}
 * method of an unbound server-socket channel will cause a {@link NotYetBoundException}
 * to be thrown. A server-socket channel can be bound by invoking one of the
 * {@link #bind(java.net.SocketAddress,int) bind} methods defined by this class.
 *
 * <p> Socket options are configured using the {@link #setOption(SocketOption,Object)
 * setOption} method. Server-socket channels support the following options:
 * <blockquote>
 * <table border summary="Socket options">
 *   <tr>
 *     <th>Option Name</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td> {@link java.net.StandardSocketOptions#SO_RCVBUF SO_RCVBUF} </td>
 *     <td> The size of the socket receive buffer </td>
 *   </tr>
 *   <tr>
 *     <td> {@link java.net.StandardSocketOptions#SO_REUSEADDR SO_REUSEADDR} </td>
 *     <td> Re-use address </td>
 *   </tr>
 * </table>
 * </blockquote>
 * Additional (implementation specific) options may also be supported.
 *
 * <p> Server-socket channels are safe for use by multiple concurrent threads.
 * </p>
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */

/*
其实NIO的主要用途是网络IO，在NIO之前java要使用网络编程就只有用Socket。
而Socket是阻塞的，显然对于高并发的场景是不适用的。所以NIO的出现就是解决了这个痛点。

主要思想是把Channel通道注册到Selector中，通过Selector去监听Channel中的事件状态，
这样就不需要阻塞等待客户端的连接，从主动等待客户端的连接，变成了通过事件驱动。
没有监听的事件，服务器可以做自己的事情。




    // 例子是阻塞式的。要做到非阻塞还需要使用选择器Selector。
    public static void main(String[] args) throws Exception {
        // 获取ServerSocketChannel
        // 通过ServerSocketChannel.open()方法可以获取服务器的通道，然后绑定一个地址端口号，
        // 接着accept()方法可获得一个SocketChannel通道，也就是客户端的连接通道。
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
        //绑定地址，端口号
        serverSocketChannel.bind(address);


        //创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true) {
            //获取SocketChannel
            SocketChannel socketChannel = serverSocketChannel.accept();
            while (socketChannel.read(byteBuffer) != -1){
                //打印结果
                System.out.println(new String(byteBuffer.array()));
                //清空缓冲区
                byteBuffer.clear();
            }
        }
    }
 */
public abstract class ServerSocketChannel
    extends AbstractSelectableChannel
    implements NetworkChannel
{

    /**
     * Initializes a new instance of this class.
     *
     * @param  provider
     *         The provider that created this channel
     */
    protected ServerSocketChannel(SelectorProvider provider) {
        super(provider);
    }

    /**
     * Opens a server-socket channel.
     *
     * <p> The new channel is created by invoking the {@link
     * java.nio.channels.spi.SelectorProvider#openServerSocketChannel
     * openServerSocketChannel} method of the system-wide default {@link
     * java.nio.channels.spi.SelectorProvider} object.
     *
     * <p> The new channel's socket is initially unbound; it must be bound to a
     * specific address via one of its socket's {@link
     * java.net.ServerSocket#bind(SocketAddress) bind} methods before
     * connections can be accepted.  </p>
     *
     * @return  A new socket channel
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public static ServerSocketChannel open() throws IOException {
        return SelectorProvider.provider().openServerSocketChannel();
    }

    /**
     * Returns an operation set identifying this channel's supported
     * operations.
     *
     * <p> Server-socket channels only support the accepting of new
     * connections, so this method returns {@link SelectionKey#OP_ACCEPT}.
     * </p>
     *
     * @return  The valid-operation set
     */
    public final int validOps() {
        return SelectionKey.OP_ACCEPT;
    }


    // -- ServerSocket-specific operations --

    /**
     * Binds the channel's socket to a local address and configures the socket
     * to listen for connections.
     *
     * <p> An invocation of this method is equivalent to the following:
     * <blockquote><pre>
     * bind(local, 0);
     * </pre></blockquote>
     *
     * @param   local
     *          The local address to bind the socket, or {@code null} to bind
     *          to an automatically assigned socket address
     *
     * @return  This channel
     *
     * @throws  AlreadyBoundException               {@inheritDoc}
     * @throws  UnsupportedAddressTypeException     {@inheritDoc}
     * @throws  ClosedChannelException              {@inheritDoc}
     * @throws  IOException                         {@inheritDoc}
     * @throws  SecurityException
     *          If a security manager has been installed and its {@link
     *          SecurityManager#checkListen checkListen} method denies the
     *          operation
     *
     * @since 1.7
     */
    public final ServerSocketChannel bind(SocketAddress local)
        throws IOException
    {
        return bind(local, 0);
    }

    /**
     * Binds the channel's socket to a local address and configures the socket to
     * listen for connections.
     *
     * <p> This method is used to establish an association between the socket and
     * a local address. Once an association is established then the socket remains
     * bound until the channel is closed.
     *
     * <p> The {@code backlog} parameter is the maximum number of pending
     * connections on the socket. Its exact semantics are implementation specific.
     * In particular, an implementation may impose a maximum length or may choose
     * to ignore the parameter altogther. If the {@code backlog} parameter has
     * the value {@code 0}, or a negative value, then an implementation specific
     * default is used.
     *
     * @param   local
     *          The address to bind the socket, or {@code null} to bind to an
     *          automatically assigned socket address
     * @param   backlog
     *          The maximum number of pending connections
     *
     * @return  This channel
     *
     * @throws  AlreadyBoundException
     *          If the socket is already bound
     * @throws  UnsupportedAddressTypeException
     *          If the type of the given address is not supported
     * @throws  ClosedChannelException
     *          If this channel is closed
     * @throws  IOException
     *          If some other I/O error occurs
     * @throws  SecurityException
     *          If a security manager has been installed and its {@link
     *          SecurityManager#checkListen checkListen} method denies the
     *          operation
     *
     * @since 1.7
     */
    public abstract ServerSocketChannel bind(SocketAddress local, int backlog)
        throws IOException;

    /**
     * @throws  UnsupportedOperationException           {@inheritDoc}
     * @throws  IllegalArgumentException                {@inheritDoc}
     * @throws  ClosedChannelException                  {@inheritDoc}
     * @throws  IOException                             {@inheritDoc}
     *
     * @since 1.7
     */
    public abstract <T> ServerSocketChannel setOption(SocketOption<T> name, T value)
        throws IOException;

    /**
     * Retrieves a server socket associated with this channel.
     *
     * <p> The returned object will not declare any public methods that are not
     * declared in the {@link java.net.ServerSocket} class.  </p>
     *
     * @return  A server socket associated with this channel
     */
    public abstract ServerSocket socket();

    /**
     * Accepts a connection made to this channel's socket.
     *
     * <p> If this channel is in non-blocking mode then this method will
     * immediately return <tt>null</tt> if there are no pending connections.
     * Otherwise it will block indefinitely until a new connection is available
     * or an I/O error occurs.
     *
     * <p> The socket channel returned by this method, if any, will be in
     * blocking mode regardless of the blocking mode of this channel.
     *
     * <p> This method performs exactly the same security checks as the {@link
     * java.net.ServerSocket#accept accept} method of the {@link
     * java.net.ServerSocket} class.  That is, if a security manager has been
     * installed then for each new connection this method verifies that the
     * address and port number of the connection's remote endpoint are
     * permitted by the security manager's {@link
     * java.lang.SecurityManager#checkAccept checkAccept} method.  </p>
     *
     * @return  The socket channel for the new connection,
     *          or <tt>null</tt> if this channel is in non-blocking mode
     *          and no connection is available to be accepted
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes this channel
     *          while the accept operation is in progress
     *
     * @throws  ClosedByInterruptException
     *          If another thread interrupts the current thread
     *          while the accept operation is in progress, thereby
     *          closing the channel and setting the current thread's
     *          interrupt status
     *
     * @throws  NotYetBoundException
     *          If this channel's socket has not yet been bound
     *
     * @throws  SecurityException
     *          If a security manager has been installed
     *          and it does not permit access to the remote endpoint
     *          of the new connection
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    public abstract SocketChannel accept() throws IOException;

    /**
     * {@inheritDoc}
     * <p>
     * If there is a security manager set, its {@code checkConnect} method is
     * called with the local address and {@code -1} as its arguments to see
     * if the operation is allowed. If the operation is not allowed,
     * a {@code SocketAddress} representing the
     * {@link java.net.InetAddress#getLoopbackAddress loopback} address and the
     * local port of the channel's socket is returned.
     *
     * @return  The {@code SocketAddress} that the socket is bound to, or the
     *          {@code SocketAddress} representing the loopback address if
     *          denied by the security manager, or {@code null} if the
     *          channel's socket is not bound
     *
     * @throws  ClosedChannelException     {@inheritDoc}
     * @throws  IOException                {@inheritDoc}
     */
    @Override
    public abstract SocketAddress getLocalAddress() throws IOException;

}
