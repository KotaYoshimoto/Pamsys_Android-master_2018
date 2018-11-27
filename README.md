# Pamsys_Android

## 通信対象のサーバのURLに関する注意
本プロジェクトでは，テスト用サーバと本番用サーバの二種類のサーバを使用した．
つまり，通信に関するテストを行う際には，テスト用サーバを示すURLを設定し，リリース時には，本番用サーバを示すURLを設定する必要がある．
設定とは，resフォルダのstrings.xml内の，"server_name"というキーを持つURLを変更することである．
以下の二種類のURLを適切に設定してほしい．
### テスト用サーバ
\<string name="server_name">http://pbl.ict.ehime-u.ac.jp/pamsys/ \</string>
### 本番用サーバ
\<string name="server_name">http://pblict.city.uwajima.ehime.jp/ \</string>
