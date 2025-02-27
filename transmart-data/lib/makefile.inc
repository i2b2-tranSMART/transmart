JDBC_DL_PSQL     := https://jdbc.postgresql.org/download
JDBC_DRIVER_PSQL := postgresql-42.2.5.jar
JDBC_DL_ORA      := http://maven.in2p3.fr/com/oracle/ojdbc7/12.1.0.1
JDBC_DRIVER_ORA  := ojdbc7-12.1.0.1.jar
JDBC_XDB_ORA     := xdb.jar

# Official Oracle driver is from:
#http://download.oracle.com/otn/utilities_drivers/jdbc/121020/ojdbc7.jar

LIB_DIR          := $(dir $(lastword $(MAKEFILE_LIST)))
CP_ORA           := $(LIB_DIR)$(JDBC_DRIVER_ORA):$(LIB_DIR)$(JDBC_XDB_ORA):$(LIB_DIR)
CP_PSQL          := $(LIB_DIR)$(JDBC_DRIVER_PSQL):$(LIB_DIR)

JDBC_DRIVER_PSQL_PATH := $(LIB_DIR)$(JDBC_DRIVER_PSQL)
JDBC_DRIVER_ORA_PATH  := $(LIB_DIR)$(JDBC_DRIVER_ORA)
JDBC_XDB_ORA_PATH     := $(LIB_DIR)$(JDBC_XDB_ORA)

$(JDBC_DRIVER_PSQL_PATH):
	curl -f $(JDBC_DL_PSQL)/$(JDBC_DRIVER_PSQL) > $@

# For correctness, the dependency on XDB should be split off since some uses of this rule don't need XDB, but it doesn't matter a lot.
$(JDBC_DRIVER_ORA_PATH): $(JDBC_XDB_ORA_PATH)
	curl -f $(JDBC_DL_ORA)/$(JDBC_DRIVER_ORA) > $@

$(JDBC_XDB_ORA_PATH):
	curl -f $(JDBC_DL_ORA)/$(JDBC_XDB_ORA) > $@

NETTY_JAR        := netty-3.5.13.Final.jar
NETTY_JAR_DL     := http://files.thehyve.net
NETTY_JAR_PATH   := $(LIB_DIR)$(NETTY_JAR)

$(NETTY_JAR_PATH):
	curl -f $(NETTY_JAR_DL)/$(NETTY_JAR) > $@

.DELETE_ON_ERROR:

# vim: ft=make list
