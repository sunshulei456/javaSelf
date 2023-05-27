/*
 * Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;
import java.lang.ref.*;

/**
 * This class extends <tt>ThreadLocal</tt> to provide inheritance of values
 * from parent thread to child thread: when a child thread is created, the
 * child receives initial values for all inheritable thread-local variables
 * for which the parent has values.  Normally the child's values will be
 * identical to the parent's; however, the child's value can be made an
 * arbitrary function of the parent's by overriding the <tt>childValue</tt>
 * method in this class.
 *
 * <p>Inheritable thread-local variables are used in preference to
 * ordinary thread-local variables when the per-thread-attribute being
 * maintained in the variable (e.g., User ID, Transaction ID) must be
 * automatically transmitted to any child threads that are created.
 *
 * @author  Josh Bloch and Doug Lea
 * @see     ThreadLocal
 * @since   1.2
 */
/*
InheritableThreadLocal继承自ThreadLocal，
在其基础上扩展了能力：不仅可在本线程内获取绑定的变量，在子线程内亦可获取到。
（请注意：必须是子线程，若你是线程池就可能不行喽，因为线程池里的线程是实现初始化好的，并不一定是你的子线程~）

它继承自ThreadLocal，所以它能力更强：通过它set进去的数据，不仅本线程内任意地方可以获取，子线程（包括子线程的子线程…）内的任意地方也都可以获取到值。

 */
public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    /**
     * Computes the child's initial value for this inheritable thread-local
     * variable as a function of the parent's value at the time the child
     * thread is created.  This method is called from within the parent
     * thread before the child is started.
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     *
     * @param parentValue the parent thread's value
     * @return the child thread's initial value
     */
    protected T childValue(T parentValue) {
        return parentValue;
    }

    /**
     * Get the map associated with a ThreadLocal.
     *
     * @param t the current thread
     */
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the table.
     */
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}
