package com.server;

import com.remote.WhiteBoard;

import java.rmi.RemoteException;

/**
 * @ClassName WhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class WhiteBoardService implements WhiteBoard {
    @Override
    public String sayHi(String name) throws RemoteException {
        return "hihi, " + name;
    }
}
