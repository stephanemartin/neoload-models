package com.neotys.neoload.model.v3.readers;

import com.neotys.neoload.model.v3.project.Project;
import com.neotys.neoload.model.v3.project.Dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Reader {
		
	protected String folder;
	private List<Dependency> dependencies = new ArrayList<>();
	
	public Reader(String folder) {
		this.folder = folder;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void addDependency(final Dependency dependency) {
		checkNotNull(dependency);
		dependencies.add(dependency);
	}

	public void removeDependencyIf(final Predicate<Dependency> filter) {
		dependencies.removeIf(filter);
	}

//	public Map<String, List<File>> getFileToCopy() {
//		Map<String, List<File>> returnedMap = new HashMap<>();
//		returnedMap.put("variables", dataFilesToCopy);
//		return returnedMap;
//	}

//	public void addDataFilesToCopy(final File file) {
//		dataFilesToCopy.add(file);
//	}
//
//	public void removeDataFilesToCopyIf(final Predicate<File> filter) {
//		dataFilesToCopy.removeIf(filter);
//	}

	public abstract Project read();
}
