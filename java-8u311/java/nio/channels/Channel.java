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
import java.io.Closeable;


/**
 * A nexus for I/O operations.
 *
 * <p> A channel represents an open connection to an entity such as a hardware
 * device, a file, a network socket, or a program component that is capable of
 * performing one or more distinct I/O operations, for example reading or
 * writing.
 *
 * <p> A channel is either open or closed.  A channel is open upon creation,
 * and once closed it remains closed.  Once a channel is closed, any attempt to
 * invoke an I/O operation upon it will cause a {@link ClosedChannelException}
 * to be thrown.  Whether or not a channel is open may be tested by invoking
 * its {@link #isOpen isOpen} method.
 *
 * <p> Channels are, in general, intended to be safe for multithreaded access
 * as described in the specifications of the interfaces and classes that extend
 * and implement this interface.
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */
/*
channel, channels, completion-handler, member-ship-key



file-lock,
xxx-channel {
    [i]: {
        asynchronous[-, byte]
        iterruptible, multicast, network,

        [-, readale, scattering, seeking, writable, gathering]-byte
    }
    [ac]: {

        asynchronous-[byte, file, server-socket, socket]
        [-, server-socket]-socket
        selectable
        datagram,
        file,
    }
}
----------------------------------------------------
gathering-byte: 一个可以聚集（Gathering）多个ByteBuffer的通道，用于将多个缓冲区的数据聚集到一个通道中。
scattering-byte: 表示一个可以分散（Scattering）多个ByteBuffer的通道，用于从一个通道中读取多个缓冲区的数据。
seeking-byte: 表示从通道指定位置开始读取。提供了在通道中定位和操作特定位置数据的方法
-------------------------------------
FileChannel，读写文件中的数据。
SocketChannel，通过TCP读写网络中的数据。在NIO之前java要使用网络编程就只有用Socket。而Socket是阻塞的，显然对于高并发的场景是不适用的
ServerSockectChannel，监听新进来的TCP连接，像Web服务器那样。对每一个新进来的连接都会创建一个SocketChannel。
DatagramChannel，通过UDP读写网络中的数据
-------------------------------------
channel
pip
selector

asynchronous 的音标是 /eɪˈsɪŋkrənəs/（发音为：eɪ-sing-krə-nəs）。
 */

/**
 * Channel本身并不存储数据，只是负责数据的运输。必须要和Buffer一起使用。
 * 输入流 --转为--> channel --写入--> buffer
  */
public interface Channel extends Closeable {

    /**
     * Tells whether or not this channel is open.
     *
     * @return <tt>true</tt> if, and only if, this channel is open
     */
    public boolean isOpen();

    /**
     * Closes this channel.
     *
     * <p> After a channel is closed, any further attempt to invoke I/O
     * operations upon it will cause a {@link ClosedChannelException} to be
     * thrown.
     *
     * <p> If this channel is already closed then invoking this method has no
     * effect.
     *
     * <p> This method may be invoked at any time.  If some other thread has
     * already invoked it, however, then another invocation will block until
     * the first invocation is complete, after which it will return without
     * effect. </p>
     *
     * @throws  IOException  If an I/O error occurs
     */
    public void close() throws IOException;

}
