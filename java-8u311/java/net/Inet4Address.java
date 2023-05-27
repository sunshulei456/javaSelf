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

package java.net;

import java.io.ObjectStreamException;

/**
 * This class represents an Internet Protocol version 4 (IPv4) address.
 * Defined by <a href="http://www.ietf.org/rfc/rfc790.txt">
 * <i>RFC&nbsp;790: Assigned Numbers</i></a>,
 * <a href="http://www.ietf.org/rfc/rfc1918.txt">
 * <i>RFC&nbsp;1918: Address Allocation for Private Internets</i></a>,
 * and <a href="http://www.ietf.org/rfc/rfc2365.txt"><i>RFC&nbsp;2365:
 * Administratively Scoped IP Multicast</i></a>
 *
 * <h3> <A NAME="format">Textual representation of IP addresses</a> </h3>
 *
 * Textual representation of IPv4 address used as input to methods
 * takes one of the following forms:
 *
 * <blockquote><table cellpadding=0 cellspacing=0 summary="layout">
 * <tr><td>{@code d.d.d.d}</td></tr>
 * <tr><td>{@code d.d.d}</td></tr>
 * <tr><td>{@code d.d}</td></tr>
 * <tr><td>{@code d}</td></tr>
 * </table></blockquote>
 *
 * <p> When four parts are specified, each is interpreted as a byte of
 * data and assigned, from left to right, to the four bytes of an IPv4
 * address.

 * <p> When a three part address is specified, the last part is
 * interpreted as a 16-bit quantity and placed in the right most two
 * bytes of the network address. This makes the three part address
 * format convenient for specifying Class B net- work addresses as
 * 128.net.host.
 *
 * <p> When a two part address is supplied, the last part is
 * interpreted as a 24-bit quantity and placed in the right most three
 * bytes of the network address. This makes the two part address
 * format convenient for specifying Class A network addresses as
 * net.host.
 *
 * <p> When only one part is given, the value is stored directly in
 * the network address without any byte rearrangement.
 *
 * <p> For methods that return a textual representation as output
 * value, the first form, i.e. a dotted-quad string, is used.
 *
 * <h4> The Scope of a Multicast Address </h4>
 *
 * Historically the IPv4 TTL field in the IP header has doubled as a
 * multicast scope field: a TTL of 0 means node-local, 1 means
 * link-local, up through 32 means site-local, up through 64 means
 * region-local, up through 128 means continent-local, and up through
 * 255 are global. However, the administrative scoping is preferred.
 * Please refer to <a href="http://www.ietf.org/rfc/rfc2365.txt">
 * <i>RFC&nbsp;2365: Administratively Scoped IP Multicast</i></a>
 * @since 1.4
 */

public final
class Inet4Address extends InetAddress {

    // inaddrsz
    final static int INADDRSZ = 4;

    /** use serialVersionUID from InetAddress, but Inet4Address instance
     *  is always replaced by an InetAddress instance before being
     *  serialized */
    private static final long serialVersionUID = 3286316764910316507L;

    /*
     * Perform initializations.
     */
    static {
        init();
    }

    Inet4Address() {
        super();
        holder().hostName = null;
        holder().address = 0;
        holder().family = IPv4;
    }

    Inet4Address(String hostName, byte addr[]) {
        holder().hostName = hostName;
        holder().family = IPv4;
        if (addr != null) {
            /*
            就是把 ip 地址改为一个
            一个 ip 地址是： 
                1.2.3.4 == addr[0001,0010,0011,0100] == address: 0000 0001 0000  0010 0000 0011 0000 0100

            核心是 ipv4 地址 32 位， 正好和 int 的 32 位重合， 让一个 int 每 8 个字节存储一个 ip 段
            0xFF: 1111 1111
            0xFF00: 1111 1111 0000 0000

            |= 操作正好是有 1 为 1， 这样将不同位置的 ip 值存入到一个 int 值中


            */
            if (addr.length == INADDRSZ) {
                // addr[3] &  11111111
                int address  = addr[3] & 0xFF;
                // 
                address |= ((addr[2] << 8) & 0xFF00);
                address |= ((addr[1] << 16) & 0xFF0000);
                address |= ((addr[0] << 24) & 0xFF000000);
                holder().address = address;
            }
        }
        holder().originalHostName = hostName;
    }
    Inet4Address(String hostName, int address) {
        holder().hostName = hostName;
        holder().family = IPv4;
        holder().address = address;
        holder().originalHostName = hostName;
    }

    /**
     * Replaces the object to be serialized with an InetAddress object.
     *
     * @return the alternate object to be serialized.
     *
     * @throws ObjectStreamException if a new object replacing this
     * object could not be created
     */
    private Object writeReplace() throws ObjectStreamException {
        // will replace the to be serialized 'this' object
        InetAddress inet = new InetAddress();
        inet.holder().hostName = holder().getHostName();
        inet.holder().address = holder().getAddress();

        /**
         * Prior to 1.4 an InetAddress was created with a family
         * based on the platform AF_INET value (usually 2).
         * For compatibility reasons we must therefore write the
         * the InetAddress with this family.
         */
        /*

        在Java 1.4之前的版本中，InetAddress 对象的创建是基于平台的 AF_INET 值（通常为2）来确定地址族（family）的。
        为了保持向后兼容性，这段代码将 InetAddress 对象的地址族设置为2。
        */
        inet.holder().family = 2;

        return inet;
    }

    /**
     * Utility routine to check if the InetAddress is an
     * IP multicast address. IP multicast address is a Class D
     * address i.e first four bits of the address are 1110.
     * @return a {@code boolean} indicating if the InetAddress is
     * an IP multicast address
     * @since   JDK1.1
     */
    /*
    is_multicast_address 是否为多播地址
    */
    public boolean isMulticastAddress() {

        /*
        adress: 1.2.3.4
        & 0xf0000000 ==> 1
        == == 0xe0000000 ==> == 1110 00000 .... ==> 
        

        */
        return ((holder().getAddress() & 0xf0000000) == 0xe0000000);
    }

    /**
     * Utility routine to check if the InetAddress in a wildcard address.
     * @return a {@code boolean} indicating if the Inetaddress is
     *         a wildcard address.
     * @since 1.4
     */
    /*
    is_any_local_address ： 用于判断一个 InetAddress 对象是否表示本地的任意地址。
    本地的任意地址是指一个特殊的 IP 地址，通常表示当前主机上的所有网络接口或所有 IP 地址。
    这个地址在IPv4中表示为 0.0.0.0，在IPv6中表示为 ::。
    */
    public boolean isAnyLocalAddress() {
        return holder().getAddress() == 0;
    }

    /**
     * Utility routine to check if the InetAddress is a loopback address.
     *
     * @return a {@code boolean} indicating if the InetAddress is
     * a loopback address; or false otherwise.
     * @since 1.4
     */
    /*
    is_loop_back_address
    在 IPv4 地址空间中，以 127 开头的地址是保留用于表示回环地址的特殊地址范围。
    具体来说，所有以 127 开头的地址均被定义为回环地址。IPv4 回环地址范围是 127.0.0.0 到 127.255.255.255

    // 一般还是使用 本机的静态 ip 替换 127.0.0.1 因为有些软件容易出问题
    127.0.0.1，也被称为本地回环地址。它用于将数据包发送给同一台计算机上的网络接口，是用于本地回环测试和通信的常见地址。


    */
    public boolean isLoopbackAddress() {
        /* 127.x.x.x */
        byte[] byteAddr = getAddress();
        return byteAddr[0] == 127;
    }

    /**
     * Utility routine to check if the InetAddress is an link local address.
     *
     * @return a {@code boolean} indicating if the InetAddress is
     * a link local address; or false if address is not a link local unicast address.
     * @since 1.4
     */
    /*
    is-link-local-address: 判断一个 InetAddress 对象是否表示链路本地地址（link-local address）
    链路本地地址是指在特定链路（例如本地局域网）上可达的本地地址。
        它们通常在无需经过路由器或因特网路由时使用，用于本地通信。

    在 IPv4 中，链路本地地址范围是 169.254.0.0 到 169.254.255.255（包括两个地址）。
        这个地址范围被保留用于链路本地地址。IPv4 链路本地地址通常由自动配置机制（例如 IPv4 的零配置或 APIPA）分配给主机，当其他网络配置方法（如 DHCP）不可用时，它们用于临时的自我配置。

    在 IPv6 中，链路本地地址通过特定的前缀 fe80::/10 表示。
        这些地址用于链路本地通信，并且在链路上具有本地唯一性。
    */
    public boolean isLinkLocalAddress() {
        // link-local unicast in IPv4 (169.254.0.0/16)
        // defined in "Documenting Special Use IPv4 Address Blocks
        // that have been Registered with IANA" by Bill Manning
        // draft-manning-dsua-06.txt
        int address = holder().getAddress();
        return (((address >>> 24) & 0xFF) == 169)
            && (((address >>> 16) & 0xFF) == 254);
    }

    /**
     * Utility routine to check if the InetAddress is a site local address.
     *
     * @return a {@code boolean} indicating if the InetAddress is
     * a site local address; or false if address is not a site local unicast address.
     * @since 1.4
     */
    /*
    is-site-local-address: 用于判断一个 InetAddress 对象是否表示站点本地地址（site-local address）。
        站点本地地址是指在一个站点或组织的范围内可达的本地地址。它们用于在特定的站点或组织内进行本地通信。

    在 IPv4 中，站点本地地址的范围,这些地址范围被保留用于站点本地地址。
            从 10.0.0.0 到 10.255.255.255，以及
            从 172.16.0.0 到 172.16.255.255，以及
            从 192.168.0.0 到 192.168.255.255。

    IPv4 站点本地地址通常在私有网络或内部网络中使用，用于局域网或组织内的通信。
    */
    public boolean isSiteLocalAddress() {
        // refer to RFC 1918
        // 10/8 prefix
        // 172.16/12 prefix
        // 192.168/16 prefix
        int address = holder().getAddress();
        return (((address >>> 24) & 0xFF) == 10)
            || ((((address >>> 24) & 0xFF) == 172)
                && (((address >>> 16) & 0xF0) == 16))
            || ((((address >>> 24) & 0xFF) == 192)
                && (((address >>> 16) & 0xFF) == 168));
    }

    /**
     * Utility routine to check if the multicast address has global scope.
     *
     * @return a {@code boolean} indicating if the address has
     *         is a multicast address of global scope, false if it is not
     *         of global scope or it is not a multicast address
     * @since 1.4
     */
    /*
    is-mg-global: 用于判断一个IPv4地址是否属于全局多播地址

    在网络中，多播地址可以划分为不同的范围，其中包括全局范围、本地范围和私有范围等。
    检查多播地址是否具有全局范围的意思是确定该地址是否能够在全球范围内进行多播通信。

    具有全局范围的多播地址可以跨越网络边界进行通信，可以在不同的网络和子网之间传输数据。
    这意味着多播数据可以从一个网络中的发送者发送，并被连接到该网络的其他多播组成员接收。

    相比之下，本地范围的多播地址仅在特定的本地网络范围内有效，不能跨越网络边界进行通信。
    私有范围的多播地址则用于特定的私有网络或组织内部使用。

    因此，通过检查多播地址是否具有全局范围，可以确定该地址是否适合在全球范围内进行多播通信。
    这在应用程序中非常有用，可以根据多播地址的范围进行适当的处理和路由选择。
    */
    public boolean isMCGlobal() {
        // 224.0.1.0 to 238.255.255.255
        byte[] byteAddr = getAddress();
        return ((byteAddr[0] & 0xff) >= 224 && (byteAddr[0] & 0xff) <= 238 ) &&
            !((byteAddr[0] & 0xff) == 224 && byteAddr[1] == 0 &&
              byteAddr[2] == 0);
    }

    /**
     * Utility routine to check if the multicast address has node scope.
     *
     * @return a {@code boolean} indicating if the address has
     *         is a multicast address of node-local scope, false if it is not
     *         of node-local scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCNodeLocal() {
        // unless ttl == 0
        return false;
    }

    /**
     * Utility routine to check if the multicast address has link scope.
     *
     * @return a {@code boolean} indicating if the address has
     *         is a multicast address of link-local scope, false if it is not
     *         of link-local scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCLinkLocal() {
        // 224.0.0/24 prefix and ttl == 1
        int address = holder().getAddress();
        return (((address >>> 24) & 0xFF) == 224)
            && (((address >>> 16) & 0xFF) == 0)
            && (((address >>> 8) & 0xFF) == 0);
    }

    /**
     * Utility routine to check if the multicast address has site scope.
     *
     * @return a {@code boolean} indicating if the address has
     *         is a multicast address of site-local scope, false if it is not
     *         of site-local scope or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCSiteLocal() {
        // 239.255/16 prefix or ttl < 32
        int address = holder().getAddress();
        return (((address >>> 24) & 0xFF) == 239)
            && (((address >>> 16) & 0xFF) == 255);
    }

    /**
     * Utility routine to check if the multicast address has organization scope.
     *
     * @return a {@code boolean} indicating if the address has
     *         is a multicast address of organization-local scope,
     *         false if it is not of organization-local scope
     *         or it is not a multicast address
     * @since 1.4
     */
    public boolean isMCOrgLocal() {
        // 239.192 - 239.195
        int address = holder().getAddress();
        return (((address >>> 24) & 0xFF) == 239)
            && (((address >>> 16) & 0xFF) >= 192)
            && (((address >>> 16) & 0xFF) <= 195);
    }

    /**
     * Returns the raw IP address of this {@code InetAddress}
     * object. The result is in network byte order: the highest order
     * byte of the address is in {@code getAddress()[0]}.
     *
     * @return  the raw IP address of this object.
     */
    public byte[] getAddress() {
        int address = holder().getAddress();
        byte[] addr = new byte[INADDRSZ];

        addr[0] = (byte) ((address >>> 24) & 0xFF);
        addr[1] = (byte) ((address >>> 16) & 0xFF);
        addr[2] = (byte) ((address >>> 8) & 0xFF);
        addr[3] = (byte) (address & 0xFF);
        return addr;
    }

    /**
     * Returns the IP address string in textual presentation form.
     *
     * @return  the raw IP address in a string format.
     * @since   JDK1.0.2
     */
    public String getHostAddress() {
        return numericToTextFormat(getAddress());
    }

    /**
     * Returns a hashcode for this IP address.
     *
     * @return  a hash code value for this IP address.
     */
    public int hashCode() {
        return holder().getAddress();
    }

    /**
     * Compares this object against the specified object.
     * The result is {@code true} if and only if the argument is
     * not {@code null} and it represents the same IP address as
     * this object.
     * <p>
     * Two instances of {@code InetAddress} represent the same IP
     * address if the length of the byte arrays returned by
     * {@code getAddress} is the same for both, and each of the
     * array components is the same for the byte arrays.
     *
     * @param   obj   the object to compare against.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     * @see     java.net.InetAddress#getAddress()
     */
    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof Inet4Address) &&
            (((InetAddress)obj).holder().getAddress() == holder().getAddress());
    }

    // Utilities
    /*
     * Converts IPv4 binary address into a string suitable for presentation.
     *
     * @param src a byte array representing an IPv4 numeric address
     * @return a String representing the IPv4 address in
     *         textual representation format
     * @since 1.4
     */

    static String numericToTextFormat(byte[] src)
    {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff) + "." + (src[3] & 0xff);
    }

    /**
     * Perform class load-time initializations.
     */
    private static native void init();
}
