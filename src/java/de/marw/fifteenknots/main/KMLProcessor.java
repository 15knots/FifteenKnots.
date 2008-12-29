
package de.marw.fifteenknots.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.marw.fifteenknots.engine.IProcessor;
import de.marw.fifteenknots.model.RaceModel;
import de.marw.fifteenknots.render.ARGBToABRGMethod;
import de.marw.fifteenknots.render.MillisToDateMethod;
import de.marw.fifteenknots.render.TemplateRenderer;
import freemarker.template.TemplateException;


/**
 * A Processor that produces output in the KML-format (Google Earth)
 *
 * @author Martin Weber
 */
class KMLProcessor implements IProcessor {

  private final Options options;

  private String outputFileName;

  private int colorCount;

  /**
   * @param globalOptions
   *        parsed global commandline options
   * @param outputFileName
   *        the name of the output file or {@code null}, if output should go to
   *        stdout.
   * @param colorCount
   *        the number of colors to use for visual boat speed coding.
   */
  public KMLProcessor( Options globalOptions, String outputFileName,
    int colorCount) {
    if (globalOptions == null) {
      throw new NullPointerException( "options");
    }
    this.options= globalOptions;
    this.outputFileName= outputFileName;
    this.colorCount= colorCount;
  }

  /**
   * @throws FileNotFoundException
   *         if one of the specified files cannot be found
   * @throws IOException
   *         If an I/O error occurs
   * @see de.marw.fifteenknots.engine.IProcessor#process()
   */
  public void process() throws FileNotFoundException, IOException {
    EncodedSpeedRaceModelBuilder builder=
      new EncodedSpeedRaceModelBuilder( options, colorCount);

    RaceModel raceModel= builder.buildModel();
    // render the output...
    Map<String, Object> model= new HashMap<String, Object>();
    model.put( "race", raceModel);
    // add conversion method to be invoked by Freemarker
    model.put( "millisToDate", new MillisToDateMethod());
    model.put( "toABGRhex", new ARGBToABRGMethod());

    // create output writer for template engine...
    Writer writer;
    {
      OutputStream out;
      if (outputFileName != null) {
	File file= new File( outputFileName);
	file.createNewFile();
	out= new BufferedOutputStream( new FileOutputStream( file));
      }
      else {
	out= System.out;
      }
      writer= new OutputStreamWriter( out, "UTF-8");
    }

    TemplateRenderer renderer=
      new TemplateRenderer( writer, "speed-colored.kml.ftl");
    try {
      renderer.process( model);
    }
    catch (TemplateException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
  }

}