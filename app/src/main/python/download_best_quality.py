import yt_dlp

def download_best_quality(url, progress_callback):
    def progress_hook(d):
        if d['status'] == 'downloading':
            downloaded_bytes = d.get('downloaded_bytes', 0)
            total_bytes = d.get('total_bytes', 1)
            progress = (downloaded_bytes / total_bytes) * 100
            progress_callback(int(progress))

    ydl_opts = {
        'format': 'best',
        'progress_hooks': [progress_hook],
        'outtmpl': '/storage/emulated/0/Download/%(title)s.%(ext)s',
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])

