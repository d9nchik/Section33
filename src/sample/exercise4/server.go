package main

import (
	"encoding/binary"
	"fmt"
	"net"
	"os"
	"sync"
	"time"
)

type numberOfVisitor struct {
	number int32
	lock sync.Mutex
}

func (nV *numberOfVisitor) increment() int32 {
	nV.lock.Lock()
	value:=nV.number
	nV.number++
	nV.lock.Unlock()
	return value
}

func (nV *numberOfVisitor) getVisitors() int32{
	return nV.number
}

func main() {
	listener, err := net.Listen("tcp", ":5050")
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	nV:=numberOfVisitor{}

	fmt.Println("Server stated at ", time.Now().Format("Jan 2006 15:04:05"))
	for {
		conn, err := listener.Accept()
		if err != nil {
			fmt.Println(err)
			continue
		}
		fmt.Println("Client from IP", conn.RemoteAddr())
		fmt.Println("Starting thread ", nV.getVisitors())
		go handleConnection(conn, nV.increment())
	}
}

func handleConnection(conn net.Conn, number int32)  {
 	defer conn.Close()
 	err:= binary.Write(conn, binary.BigEndian, number)
	if err != nil {
		fmt.Println(err)
	}
}
