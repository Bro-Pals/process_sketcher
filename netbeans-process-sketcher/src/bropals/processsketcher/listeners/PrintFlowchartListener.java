/*
 * Process Sketcher is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Process Sketcher.
 *
 * Process Sketcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Process Sketcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Process Sketcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.processsketcher.listeners;

import bropals.processsketcher.FlowchartWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.swing.JOptionPane;

/**
 * The listener for the print flowchart button.
 * @author Jonathon
 */
public class PrintFlowchartListener extends AbstractProcessSketcherListener implements ActionListener {

    public PrintFlowchartListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (PrinterJob.lookupPrintServices().length > 0) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Process Sketcher: " + getFlowchartWindow().getFlowchartName());
            getFlowchartWindow().getFlowchart().passInstance(getFlowchartWindow());
            PageFormat instance = new PageFormat();
            PageFormat old = instance;
            
            if ( (instance = job.pageDialog(old)) != old) {
                getFlowchartWindow().showPrintPreview(
                    (int)(instance.getImageableX()),
                    (int)(instance.getWidth()-instance.getImageableX()-instance.getImageableWidth()),
                    (int)(instance.getImageableY()),
                    (int)(instance.getHeight()-instance.getImageableY()-instance.getImageableHeight()),
                    (int)(instance.getImageableWidth()),
                    (int)(instance.getImageableHeight()),
                    job
                );
            }
        } else {
            JOptionPane.showMessageDialog(getFlowchartWindow(), "There are no printers on this computer.", "Can't print", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
