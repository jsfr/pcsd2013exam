#!/bin/bash
xterm -e "cd $HOME/repos/pcsd2013exam/acertainsupplychain/bin && java -classpath '.:../lib/*' com.acertainsupplychain.server.ItemSupplierHTTPServer 1 8083" &
xterm -e "cd $HOME/repos/pcsd2013exam/acertainsupplychain/bin && java -classpath '.:../lib/*' com.acertainsupplychain.server.ItemSupplierHTTPServer 0 8082" &
xterm -e "cd $HOME/repos/pcsd2013exam/acertainsupplychain/bin && java -classpath '.:../lib/*' com.acertainsupplychain.server.OrderManagerHTTPServer" &