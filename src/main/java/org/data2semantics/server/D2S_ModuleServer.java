package org.data2semantics.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.data2semantics.exception.D2S_ModuleException;
import org.data2semantics.modules.D2S_AbstractModule;
import org.data2semantics.modules.D2S_ModuleWrapper;
import org.data2semantics.util.D2S_RepositoryWriter;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D2S_ModuleServer extends HttpServlet {


	private static final long serialVersionUID = 4287719806326260156L;
	private static Logger log = LoggerFactory.getLogger(D2S_ModuleWrapper.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String moduleName = request.getParameter("module");
		String fileName = request.getParameter("file");
		String graph = request.getParameter("graph");
		String resource = request.getParameter("resource");

		try {
			D2S_AbstractModule module = D2S_ModuleWrapper.constructModule(moduleName,
					fileName, graph, resource);

			log.info("Starting module");
			Repository outputRepository = module.start();
			log.info("Module run completed");

			// TODO Add provenance information about ModuleWrapper run
			
			String outputFileName = "output.n3";
			try {
				log.info("Starting RepositoryWriter (writing to " + outputFileName
						+ ")");

				FileOutputStream outputStream = new FileOutputStream(new File(
						outputFileName));
				OutputStreamWriter streamWriter = new OutputStreamWriter(
						outputStream);
				D2S_RepositoryWriter rw = new D2S_RepositoryWriter(outputRepository, streamWriter);

				rw.write();
				log.info("Done");

			} catch (FileNotFoundException e) {
				log.error("Failed to create output file " + outputFileName);
			}
			
			// TODO: Fix the code below, as it runs out of memory...
			
//			log.info("Starting RepositoryWriter (writing to string)");
//
//			StringWriter stringWriter = new StringWriter();
//			RepositoryWriter rw = new RepositoryWriter(outputRepository,
//					stringWriter);
//
//			rw.write();
//			log.info("Done");
//
//			String output = stringWriter.toString();

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("All done!<br/>");
			response.getWriter().println(
					"session=" + request.getSession(true).getId());
			D2S_ModuleException e;
		} catch (D2S_ModuleException e) {
			log.error("There is a problem in instantiating module : " +e.getMessage());
		}

	}

}
