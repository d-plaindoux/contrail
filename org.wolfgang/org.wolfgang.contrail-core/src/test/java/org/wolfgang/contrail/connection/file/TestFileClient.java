/*
 * Copyright (C)2012 D. Plaindoux.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.wolfgang.contrail.connection.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataHandler;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.StreamDataHandlerFactory;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerAdapter;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestFileClient</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestFileClient extends TestCase {

	private String getContent(File file) throws IOException {
		final InputStream inputStream = new FileInputStream(file);
		final StringBuilder builder = new StringBuilder();

		final byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				builder.append(new String(buffer, 0, len));
			}
		} finally {
			inputStream.close();
		}

		return builder.toString();
	}

	@Test
	public void testNominal01() throws IOException, CannotCreateDataHandlerException, InterruptedException, ExecutionException {
		final File input = new File("src/test/Sample.txt");
		final File output = new File("src/test/Sample.txt.out");

		final OutputStream outputStream = new FileOutputStream(output);

		final UpStreamDataHandler<byte[]> terminalReceiver = StreamDataHandlerFactory.<byte[]> create(new UpStreamDataHandler<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataHandlerException {
				try {
					outputStream.write(data);
					outputStream.flush();
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				try {
					outputStream.close();
				} catch (IOException e) {
					throw new DataHandlerCloseException(e);
				}
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				handleClose();
			}
		});

		final UpStreamDataHandlerFactory<byte[], byte[]> destinationComponentFactory = new UpStreamDataHandlerFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataHandler<byte[]> create(DownStreamDataHandler<byte[]> initialReceiver) throws CannotCreateDataHandlerException {
				final InitialComponent<byte[], byte[]> initialComponent = new InitialComponent<byte[], byte[]>(initialReceiver);
				final TerminalComponent<byte[], byte[]> terminalComponent = new TerminalComponent<byte[], byte[]>(terminalReceiver);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataHandlerException(e);
				}
				return InitialUpStreamDataHandler.<byte[]> create(initialComponent);
			}
		};

		final InputStreamClient fileSender = new InputStreamClient(destinationComponentFactory);
		final InputStream inputStream = new FileInputStream(input);

		try {
			fileSender.connect(inputStream).get();
			fileSender.close();
		} finally {
			inputStream.close();
		}

		// Compare files now
		assertEquals(getContent(input), getContent(output));
		output.delete();
	}

	@Test
	public void testNominal02() throws IOException, CannotCreateDataHandlerException, InterruptedException, ExecutionException, DataHandlerException, DataHandlerCloseException {
		final File input = new File("src/test/Sample.txt");
		final File output = new File("src/test/Sample.txt.out");

		final AtomicReference<UpStreamDataHandler<byte[]>> atomicReference = new AtomicReference<UpStreamDataHandler<byte[]>>();
		final UpStreamDataHandlerFactory<byte[], byte[]> terminalReceiverFactory = new UpStreamDataHandlerFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataHandler<byte[]> create(final DownStreamDataHandler<byte[]> sender) {
				final UpStreamDataHandler<byte[]> dataReceiver = new UpStreamDataHandlerAdapter<byte[]>() {
					@Override
					public void handleData(byte[] data) throws DataHandlerException {
						sender.handleData(data);
					}
				};

				atomicReference.set(dataReceiver);

				return dataReceiver;
			}
		};

		final UpStreamDataHandlerFactory<byte[], byte[]> destinationComponentFactory = new UpStreamDataHandlerFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataHandler<byte[]> create(DownStreamDataHandler<byte[]> initialReceiver) throws CannotCreateDataHandlerException {
				final InitialComponent<byte[], byte[]> initialComponent = new InitialComponent<byte[], byte[]>(initialReceiver);
				final TerminalComponent<byte[], byte[]> terminalComponent = new TerminalComponent<byte[], byte[]>(terminalReceiverFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataHandlerException(e);
				}
				return InitialUpStreamDataHandler.<byte[]> create(initialComponent);
			}
		};

		final OutputStreamClient fileReceiver = new OutputStreamClient(destinationComponentFactory);
		final FileOutputStream fileOutputStream = new FileOutputStream(output);

		fileReceiver.connect(fileOutputStream).get();

		final InputStream inputStream = new FileInputStream(input);

		final byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				atomicReference.get().handleData(Arrays.copyOf(buffer, len));
			}
		} finally {
			inputStream.close();
		}

		fileReceiver.close();

		// Compare files now
		assertEquals(getContent(input), getContent(output));
		output.delete();
	}
}
