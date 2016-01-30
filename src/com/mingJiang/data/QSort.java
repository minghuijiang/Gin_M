package com.mingJiang.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mingJiang.util.FileUtil;

public class QSort {

	public static void main(String[] args) throws IOException{
		List<Pair<Long,String>> list = new ArrayList<>();
		for(String s: FileUtil.readFrom("C:/Users/Ming Jiang/Desktop/GIN-1000.txt")){
			String[] sp = s.split("----");
			long i = Long.parseLong(sp[0]);
			list.add(new Pair<>(i,s));
		}
		
		Collections.sort(list, new Comparator<Pair<Long,String>>(){

			@Override
			public int compare(Pair<Long, String> o1,
					Pair<Long, String> o2) {
				if(o1.getKey()>o2.getKey())
					return 1;
				if(o1.getKey()<o2.getKey())
					return -1;
				return 0;
			}

			
		});
		List<String> data = new ArrayList<>();
		for(Pair<Long,String> p: list)
			data.add(p.getObj());
		
		FileUtil.writeTo("C:/Users/Ming Jiang/Desktop/GIN-10002.txt", data);
	}
}
