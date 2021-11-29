/**
 * 
 */
package com.hma.vmma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class Student {
	private int id;
	private String name;
	private boolean internal;
	
	public static void main(String args[]) {
		List<Student> students = new ArrayList<Student>();
		
		for (int i = 0; i < 5; i++) {
			Student student = new Student();
			student.setId((int)(Math.round(Math.random()*5)) + 1);
			student.setName(String.format("student%d", i));
			student.setInternal(i % 2 == 0 ? true : false);
			students.add(student);
		}
		
		for(Student student : students) {
			System.out.println(student.toString());
		}

		Collections.sort(students, new Comparator<Student>() {

			@Override
			public int compare(Student o1, Student o2) {
				if(o1.isInternal() && !o2.isInternal()) {
					return -1;
				}
				if(!o1.isInternal() && o2.isInternal()) {
					return 0;
				}
				
				
				return o1.getId() - o2.getId();
			}
			
		});
		for(Student student : students) {
			System.out.println(student.toString());
		}
	}
}
