from flask import Flask, jsonify, request
import yt_dlp
import subprocess

app = Flask(__name__)

def get_youtube_video_url(youtube_url):
    result = subprocess.run(['yt-dlp', '-f', 'bestvideo+bestaudio', '--get-url', youtube_url], capture_output=True, text=True)
    return result.stdout.strip().split('\n')

@app.route('/get_video_info', methods=['GET'])
def get_video_info():
    key = request.args.get('key')
    if not key:
        return jsonify({'error': 'Missing KEY parameter'}), 400

    youtube_url = f"https://www.youtube.com/watch?v={key}"
    print(key)
    print(youtube_url)

    try:
        video_urls = get_youtube_video_url(youtube_url)
        video_url, audio_url = video_urls[0], video_urls[1]
        return jsonify({
            'video_url': video_url,
            'audio_url': audio_url
            })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)
