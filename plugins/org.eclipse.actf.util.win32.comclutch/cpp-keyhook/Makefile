###############################################################################
# Copyright (c) 2007 IBM Corporation and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Hisashi MIYASHITA - initial API and implementation
###############################################################################

BASEPKG = org.eclipse.actf.util.win32.keyhook
BASEIMPLPKG = $(BASEPKG).impl
JAVAH = "$(JAVA_HOME:/=\)\bin\javah"
JAVAH_FLAGS = -classpath ../bin
JAVA_INC = "$(JAVA_HOME)/include"
BLD = ..\build
CC = cl.exe
CFLAGS = -c -Zi -Ox -I $(JAVA_INC)
LINK = link.exe
LDFLAGS = -debug -dll
LIBS = User32.lib

OBJS = $(BLD)\KeyHook.obj $(BLD)\SendEvent.obj $(BLD)\WindowHook.obj
DLL = ../KeyHook.dll

all: init headers $(DLL)

init:
	-mkdir $(BLD)

clean:
	-rd /S/Q $(BLD)

headers: 
	$(JAVAH) $(JAVAH_FLAGS) $(BASEIMPLPKG).KeyHookImpl $(BASEIMPLPKG).SendEventImpl

$(DLL): $(OBJS)
	$(LINK) $(LDFLAGS) $(OBJS) -out:$(DLL) $(LIBS)

.c{$(BLD)}.obj:
	$(CC) $(CFLAGS) -Fo$(BLD)\ $<


