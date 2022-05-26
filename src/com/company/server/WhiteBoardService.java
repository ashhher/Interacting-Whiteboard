package com.company.server;

import java.rmi.RemoteException;

/**
 * @ClassName WhiteBoard
 * @Description TODO
 * @Author XiaoHan
 **/
public class WhiteBoardService implements com.company.remote.WhiteBoard {
    @Override
    public String sayHi(String name) throws RemoteException {
        return "hihi, " + name;
    }
}
