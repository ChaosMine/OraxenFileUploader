package dev.kamillaova.oraxen_file_uploader.oraxen;

import com.google.common.hash.Hashing;
import io.th0rgal.oraxen.pack.upload.hosts.HostingProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.requireNonNull;

public final class FileUploader implements HostingProvider {
	private final String url;
	private final Path filePath;
	private byte[] sha1;
	private String sha1String;

	public FileUploader(ConfigurationSection options) {
		this.url = URI.create(requireNonNull(options.getString("url"))).toASCIIString();
		this.filePath = Path.of(requireNonNull(options.getString("file_path")));
	}

	@Override
	@SuppressWarnings("deprecation") // Guava has deprecated sha1 ¯\_(ツ)_/¯
	public boolean uploadPack(File packFile) {
		var packPath = packFile.toPath();

		try (
			var packFc = FileChannel.open(packPath, StandardOpenOption.READ);
			var fileFc = FileChannel.open(
				this.filePath,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE
			)
		) {
			var packSize = packFc.size();

			var packBuffer = packFc.map(MapMode.READ_ONLY, 0L, packSize);

			var hash = Hashing.sha1().hashBytes(packBuffer);

			this.sha1 = hash.asBytes();
			this.sha1String = hash.toString();

			// Probably a workaround for copying to the bindmounted file
			if (packFc.transferTo(0, packSize, fileFc) != packSize) {
				throw new IOException("Failed to write to file (length does not match)");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public String getPackURL() {
		return this.url;
	}

	@Override
	public String getMinecraftPackURL() {
		return this.url;
	}

	@Override
	public byte[] getSHA1() {
		return this.sha1;
	}

	@Override
	public String getOriginalSHA1() {
		return this.sha1String;
	}
}
