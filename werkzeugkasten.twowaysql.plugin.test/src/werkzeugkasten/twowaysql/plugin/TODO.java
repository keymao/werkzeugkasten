package werkzeugkasten.twowaysql.plugin;

public interface TODO {

	// fixed パーティショナの実装
	// fixed Lexerを使わないパーティショナの実装
	// fixed 色分け
	// - 色分け定義の変更、表示フォントの変更
	// working パーザエラーメッセージの表示
	// - BEGIN ~ END,IF ~ ELSEIF ~ ELSE ~ END 用のStackをエラーメッセージ出力用に管理する。
	// - EL式のリアルタイムバリデータ。
	// -- カーソルがある位置のパーティションは編集中なのでバリデーション対象外にする。
	// -- 条件部分は、最終結果がbooleanになっていないといけない。
	// -- EL式内で、最終結果の型がメソッドの時には、呼び忘れなので、エラーを出力する。
	// - エラーメッセージに対する変更候補表示+自動変更
	// fixed コメント部分の入力補完
	// working 式言語部分の入力補完
	// working マルチページエディタ化(入力候補の登録)
	// working 引数名からエディタの入力補完候補の自動登録
	// - interfaceからSQLプロセッサの自動生成
	// - interfaceに定義されたSQLファイルが存在しない場合にエラー表示
	// - リファクタリング追従
	// - 単純実行するJUnitテストの自動生成
	// - 対応関係のないSQLファイルの検索ツール
	// - .sqlファイルのデバッグ実行。引数にテキトウな値を設定して、最終的に動作するSQLをステップ実行しながら確認出来る。
	// - IFコメント入力時に、TABキーで、カーソル位置をジャンプする。JDTの Java -> Editor -> Templates 相当の機能
	// - 式言語部分にマウスカーソルが当たっている時に、当該JavaElementのjavadoc等を表示する。
	// - ProjectionViewer を使って、対応する括弧ごと開いたり閉じたりする。
	// -- ProjectionAnnotationModel,ProjectionAnnotation
	// - ITextHoverで、カーソルが当たっている式言語内の型に関するjavadocをホバーする。
	// -- org.eclipse.jface.text.TextViewerHoverManager
	// -- org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover
	// - .sqlファイルのフォーマッタ機能
	// -- org.eclipse.jface.text.formatter.IContentFormatter
	// -- パーザベースでやるなら、パーザ自体を作りなおさないとダメかも。
}
