import yt_dlp

def download_video(url, download_path):
    options = {
        'format': 'best',  # Download the best quality available
        'outtmpl': download_path  # Specify the download path
    }

    with yt_dlp.YoutubeDL(options) as ydl:
        ydl.download([url])  # Download the video from the URL